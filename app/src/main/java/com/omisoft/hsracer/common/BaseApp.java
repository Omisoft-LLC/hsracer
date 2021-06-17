package com.omisoft.hsracer.common;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.omisoft.hsracer.constants.Constants.TIME_REPEATING;
import static com.omisoft.hsracer.constants.Constants.TRIGGER_REPEATING;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import com.facebook.stetho.Stetho;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.huma.room_for_asset.RoomAsset;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.EventBusIndex;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.common.utils.AuthorizationInterceptor;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.db.AppDatabase;
import com.omisoft.hsracer.db.AppReferenceDatabase;
import com.omisoft.hsracer.features.race.events.RaceGPSEvent;
import com.omisoft.hsracer.features.race.events.RaceOBDEvent;
import com.omisoft.hsracer.features.race.events.RaceWebSocketEvent;
import com.omisoft.hsracer.features.race.structures.GPSDataDTO;
import com.omisoft.hsracer.features.race.structures.OBDDataDTO;
import com.omisoft.hsracer.features.race.structures.RaceDataDTO;
import com.omisoft.hsracer.services.UploadVideoService;
import com.omisoft.hsracer.utils.SDCardUtils;
import com.omisoft.hsracer.utils.Utils;
import com.omisoft.hsracer.ws.protocol.dto.RaceStatusDTO;
import com.squareup.leakcanary.LeakCanary;
import com.yakivmospan.scytale.Crypto;
import com.yakivmospan.scytale.Options;
import com.yakivmospan.scytale.Store;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Cache;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import org.greenrobot.eventbus.EventBus;
// TODO Move all classes from package common to library

/**
 * Base App Singleton Utility Class
 * Created by dido on 20.04.17.
 */

public class BaseApp extends MultiDexApplication {

  public static final int NOTIFICATION_ID = 42;
  private static final String HSRACER_DATABASE = "hsracer-database";
  private OkHttpClient httpClient;
  private AppDatabase db;
  private AppReferenceDatabase referenceDatabase;
  private ExecutorService executorService;
  private EventBus eventBus;
  private static final String TAG = BaseApp.class.getName();
  @Getter
  private Crypto crypto;


  public static BaseApp get(Context context) {
    return (BaseApp) context.getApplicationContext();
  }

  @Getter
  @Setter
  private volatile boolean GPSFix;
  @Getter
  private RaceDataManager raceDataManager;


  private ObjectMapper mapper;

  @Override
  public void onCreate() {
    super.onCreate();

    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
          .detectAll()   // or .detectAll() for all detectable problems
          .penaltyLog()
          .build());
      StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
          .detectLeakedSqlLiteObjects()
          .detectLeakedClosableObjects()
          .detectAll()
          .penaltyLog()
          .build());
      LeakCanary.install(this);
      Stetho.initializeWithDefaults(this);
    }
    eventBus = EventBus.builder().addIndex(new EventBusIndex()).installDefaultEventBus();

    mapper = new ObjectMapper();
    // init okhttp
    AuthorizationInterceptor myInterceptor = new AuthorizationInterceptor();
    OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
    okhttpBuilder.connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS).addInterceptor(myInterceptor);
    if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {

      ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
          .tlsVersions(TlsVersion.TLS_1_2)
          .cipherSuites(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
              CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
              CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
          .build();
      List<ConnectionSpec> specList = new ArrayList<>();
      specList.add(spec);
      specList.add(ConnectionSpec.CLEARTEXT);
      okhttpBuilder.connectionSpecs(specList);
    }

// Build http client
    httpClient = okhttpBuilder.build();
    // Create executor service for network, io and db code.
    executorService = Executors.newFixedThreadPool(10);
    // init keystore
    initKeystore();

    // init shared prefs
    initSharedPrefs();
    // Init utils lib
    Utils.init(this);
    // Init singleton RaceDataManager
    raceDataManager = new RaceDataManager();
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        getDB();
        getReferenceDB();
        try {
          copyFilesToSdCard();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    thread.start();
  }

  private void copyFilesToSdCard() throws IOException {
    File file = new File(SDCardUtils.getSDCardPath() + getString(R.string.app_name) + File.separator
        + Constants.NO_STREAM_FILE);
    if (!file.exists()) {
      Log.wtf(TAG, String
          .valueOf(new File(SDCardUtils.getSDCardPath() + getString(R.string.app_name)).mkdirs()));

      InputStream in = getResources().openRawResource(R.raw.no_stream);
      FileOutputStream out = new FileOutputStream(
          file);
      byte[] buff = new byte[1024];
      int read = 0;

      try {
        while ((read = in.read(buff)) > 0) {
          out.write(buff, 0, read);
        }
      } finally {
        in.close();
        out.close();
      }
    }
  }

  private void initKeystore() {
// Create and save key
    Store store = new Store(getApplicationContext());
    if (!store.hasKey(BuildConfig.APPLICATION_ID)) {
      store.generateAsymmetricKey(BuildConfig.APPLICATION_ID, null);
    }
    crypto = new Crypto(Options.TRANSFORMATION_ASYMMETRIC);
  }

  /**
   * Init shared prefs with default values
   */
  private void initSharedPrefs() {
    // Add shared prefs init here
  }


  public ObjectMapper getObjectMapper() {
    return mapper;
  }


  public OkHttpClient getHttpClient() {
    return httpClient;
  }

  public AppDatabase getDB() {
    if (db == null || !db.isOpen()) {
      db = Room.databaseBuilder(getApplicationContext(),
          AppDatabase.class, HSRACER_DATABASE).addMigrations(PROFILE_MIGRATION).build();
    }
    return db;

  }

  static final Migration PROFILE_MIGRATION = new Migration(1, 2) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL(
          "CREATE TABLE profile ( _id INTEGER NOT NULL, email TEXT, password TEXT, personalId TEXT, alias TEXT, firstName TEXT, lastName TEXT, age INTEGER, city TEXT, country TEXT, PRIMARY KEY(_id))");
      database.execSQL(
          "CREATE TABLE car ( _id INTEGER NOT NULL, manufacturer TEXT, model TEXT, alias TEXT, year INTEGER, engine INTEGER, volume INTEGER, hpr INTEGER, fuel TEXT, profileId INTEGER, carId TEXT, PRIMARY KEY(_id))");
      database.execSQL(
          "CREATE TABLE race ( _id INTEGER, name TEXT, description TEXT, locationId INTEGER, raceTimeInMills INTEGER, carTelemetryId INTEGER, gpsDataId INTEGER, videoDataId INTEGER, raceDistance INTEGER, finishPosition INTEGER, carId INTEGER, profileId INTEGER, PRIMARY KEY(_id))");
      database.execSQL(
          "CREATE TABLE videodata ( _id INTEGER, videoURL TEXT, PRIMARY KEY(_id))");
      database.execSQL(
          "CREATE TABLE carTelemetry ( _id INTEGER, meanSpeed INTEGER, maxSpeed INTEGER, totalDistance INTEGER, PRIMARY KEY(_id))");
      database.execSQL(
          "CREATE TABLE locationData ( _id INTEGER, time INTEGER, raceId TEXT,latitude REAL, longitude REAL, distance REAL, PRIMARY KEY(_id))");
    }
  };


  public AppReferenceDatabase getReferenceDB() {
    if (referenceDatabase == null || !referenceDatabase.isOpen()) {
      referenceDatabase = RoomAsset.databaseBuilder(getApplicationContext(),
          AppReferenceDatabase.class, "ref.db").build();

    }
    return referenceDatabase;
  }


  public ExecutorService getExecutor() {
    return executorService;
  }

  public EventBus getEventBus() {

    return eventBus;
  }


  protected Cache getHttpCache(Application application) {
    int cacheSize = 10 * 1024 * 1024;
    return new Cache(application.getCacheDir(), cacheSize);
  }

  public String getAuthId() {

    SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
    return sharedPreferences.getString(Constants.AUTH_ID, "");
  }

  /**
   * Checks if we have permissions
   */
  public boolean hasPermission(String perm) {
    return VERSION.SDK_INT < VERSION_CODES.M || (PackageManager.PERMISSION_GRANTED
        == checkSelfPermission(perm));
  }

  @Getter
  @Setter
  public static class Permissions {

    public static boolean ALL_PERMISSIONS = false;
    public static boolean GPS = false;
    public static boolean Video = false;
    public static boolean WriteToMemory = false;
    public static boolean EspressoTest = false;
  }

  /**
   * Clear shared preferences used by race
   */
  public void clearRaceSharedPreferences() {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.remove(Constants.CAR_REST_ID);
    editor.remove(Constants.LOCAL_CAR_ID);
    editor.remove(Constants.RACE_ID);
    editor.remove(Constants.CREATOR);
    editor.remove(Constants.SHOW_PREVIEW);
    editor.remove(Constants.RECORD_VIDEO);
    editor.remove(Constants.LIVE_OBD_DATA);
    editor.remove(Constants.LIVE_STREAM);
    editor.remove(Constants.RACE_IS_RUNNING);
    editor.apply();
  }

  /**
   * Returns default shared preferences
   */
  public SharedPreferences getSharedPreferences() {
    return PreferenceManager.getDefaultSharedPreferences(this);
  }

  /**
   * Check if we have network connectivity
   */
  public boolean isConnected() {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }

  /**
   * Check if GPS is enabled
   */
  public boolean checkIsGPSEnabled() {
    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  /**
   * Creates pending intent for current activity sets it to notification
   */
  public static PendingIntent prepareIntentForNotification(Context context,
      Class<? extends BaseActivity> destinationActivity) {
    Intent intent = new Intent(context, destinationActivity);
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

    return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  /**
   * Create WS
   * NB: Call on thread != UI Thread
   */
  public WebSocket createWebSocket(String url) {
    WebSocketFactory factory = new WebSocketFactory();
    WebSocket ws = null;
    try {
      ws = factory.createSocket(url);
      ws.addExtension(WebSocketExtension.PERMESSAGE_DEFLATE);
      ws.setPingInterval(60 * 1000);
      ws.setAutoFlush(true);


    } catch (IOException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_generic, e, this.getClass().getSimpleName()));
    }
    return ws;
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  public class RaceDataManager {

    @Setter
    private volatile RaceGPSEvent gpsData;
    @Setter
    private volatile RaceWebSocketEvent webSocketData;
    @Setter
    private volatile RaceOBDEvent obdData;
    @Setter
    @Getter
    private volatile long raceStartTime;
    @Getter
    @Setter
    private volatile String raceVideoPath;
    private Deque<RaceDataDTO> raceDataDTOList;
    @Getter
    @Setter
    private volatile RaceStatusDTO lastRaceStatusMessage;


    public RaceDataManager() {
      raceDataDTOList = new ArrayDeque<>();
    }


    public RaceDataDTO getCurrentRaceData() {
      RaceDataDTO raceDataDTO = new RaceDataDTO();
      raceDataDTO.setRaceCurrentTime(SystemClock.elapsedRealtime() - raceStartTime);

      if (gpsData != null) {
        Message gpsMessage = gpsData.getMessage();
        GPSDataDTO gpsDataDTO = gpsMessage.getData().getParcelable(GPSDataDTO.class.getName());
        if (gpsDataDTO != null) {
          raceDataDTO.setGpsSpeed(gpsDataDTO.getSpeed());
          raceDataDTO.setGpsAcceleration(gpsDataDTO.getAcceleration());

          raceDataDTO.setGpsDistance(gpsDataDTO.getDistance());
          raceDataDTO.setLongitude(gpsDataDTO.getLongitude());
          raceDataDTO.setLatitude(gpsDataDTO.getLatitude());
        }
        if (obdData != null) {
          Message obdMessage = obdData.getMessage();
          Bundle obdBundle = obdMessage.getData();
          OBDDataDTO obdDataDTO = obdBundle.getParcelable(OBDDataDTO.class.getName());
          if (obdDataDTO != null) {
            raceDataDTO.setObdSpeed(obdDataDTO.getObdSpeed());
            raceDataDTO.setObdRpm(obdDataDTO.getObdRpm());
            raceDataDTO.setEngineCoolantTemp(obdDataDTO.getEngineCoolantTemp());
          }
        }

        raceDataDTOList.add(raceDataDTO);
      }
      return raceDataDTO;
    }

    public List<RaceDataDTO> getCurrentRaceDataList() {
      return new LinkedList<>(raceDataDTOList);
    }

    public void clear() {
      raceDataDTOList.clear();
      gpsData = null;
      obdData = null;
      webSocketData = null;
    }
  }

  /**
   * Start a service that will upload every video that is checked in UploadVideoActivity or selected
   * to be uploaded in the Gallery from the android device
   */
  public void startUploadVideoService() {
    try {
      Intent uploadingService = new Intent(this, UploadVideoService.class);
      PendingIntent intent = PendingIntent.getService(this, 0, uploadingService, 0);
      AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
      if (alarm != null) {
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, TRIGGER_REPEATING, TIME_REPEATING,
            intent);
      }
    } catch (Exception e) {
      Log.wtf(TAG, "onServiceConnected: ", e);
    }
  }

  public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
    return new DefaultDataSourceFactory(this, bandwidthMeter,
        buildHttpDataSourceFactory(bandwidthMeter));
  }

  public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
    return new DefaultHttpDataSourceFactory(BuildConfig.APPLICATION_ID, bandwidthMeter);
  }
}
