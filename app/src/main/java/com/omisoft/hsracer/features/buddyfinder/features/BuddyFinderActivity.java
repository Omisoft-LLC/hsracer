package com.omisoft.hsracer.features.buddyfinder.features;

import static com.omisoft.hsracer.constants.Constants.DEFAULT_NICK_NAME;
import static com.omisoft.hsracer.constants.Constants.JOINED_FINDER;
import static com.omisoft.hsracer.constants.Constants.NULL_NICK_NAME;
import static com.omisoft.hsracer.constants.Constants.RECEIVED_MESSAGE;
import static com.omisoft.hsracer.constants.Constants.RECEIVER_RACERS_PULL;
import static com.omisoft.hsracer.features.buddyfinder.BuddyFinderUtils.getBoundingBox;

import android.Manifest.permission;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.Toast;
import butterknife.BindView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.constants.FirebaseEvent;
import com.omisoft.hsracer.features.buddyfinder.dto.JoinFinderDTO;
import com.omisoft.hsracer.features.buddyfinder.events.BuddyFinderLifeCycleCommandEvent;
import com.omisoft.hsracer.features.buddyfinder.events.BuddyFinderLocationEvent;
import com.omisoft.hsracer.features.buddyfinder.events.BuddyFinderWebSocketEvent;
import com.omisoft.hsracer.features.buddyfinder.listener.BuddyFinderLocationListener;
import com.omisoft.hsracer.features.buddyfinder.threads.BuddyFinderWebSocketThread;
import com.omisoft.hsracer.services.RaceService;
import com.omisoft.hsracer.utils.AndroidUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Marker.OnMarkerClickListener;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

/**
 * Finds racing buddies
 */
public class BuddyFinderActivity extends BaseActivity {

  private IMapController mapController;
  @BindView(R.id.map)
  MapView mapView;
  private MyLocationNewOverlay overlay;
  private volatile double longitude = 42.6636;
  private volatile double latitude = 23.2913;
  private List<JoinFinderDTO> racers;
  private int joinBuddyFinder;
  private String nickname;
  private FusedLocationProviderClient googleFusedProvider;
  private BuddyFinderLocationListener buddyFinderLocationListener;
  private static final long UPDATE_INTERVAL = 30 * 1000;
  private static final long FASTEST_INTERVAL = 2 * 1000;
  private ReceivedMessageDialogFragment dialog;
  private LocationRequest locationRequest;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_buddy_finder);
    dialog = new ReceivedMessageDialogFragment();
    startBuddyFinderListener();
    initMap();

    if (savedInstanceState == null) {
      joinBuddyFinder = 0;
    }

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    nickname = sharedPreferences.getString(Constants.NICK_NAME, "");
  }

  @Override
  public void onStop() {
    super.onStop();
    if (googleFusedProvider != null) {
      googleFusedProvider.removeLocationUpdates(buddyFinderLocationListener);
    }
    if (dialog != null) {
      if (dialog.getDialog() != null) {
        dialog.getDialog().dismiss();
        dialog = null;
      }
    }
    leaveBuddyFinder();
    disconnectSocket();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    leaveBuddyFinder();
    disconnectSocket();
  }

  /**
   * Sets the buddy finder properties about the map and GPS. Also in the end of that method is asked
   * for permissions
   */
  private void startBuddyFinderListener() {

    locationRequest = new LocationRequest();
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    locationRequest.setInterval(UPDATE_INTERVAL);
    locationRequest.setFastestInterval(FASTEST_INTERVAL);
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
    builder.addLocationRequest(locationRequest);
    LocationSettingsRequest locationSettingsRequest = builder.build();
    SettingsClient settingsClient = LocationServices.getSettingsClient(this);
    settingsClient.checkLocationSettings(locationSettingsRequest);
    buddyFinderLocationListener = new BuddyFinderLocationListener();
    googleFusedProvider = new FusedLocationProviderClient(this);
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      Toast.makeText(BuddyFinderActivity.this, "Can not locate position", Toast.LENGTH_SHORT)
          .show();
    } else {
      googleFusedProvider
          .requestLocationUpdates(locationRequest, buddyFinderLocationListener,
              Looper.myLooper());
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    googleFusedProvider
        .requestLocationUpdates(locationRequest, buddyFinderLocationListener, Looper.myLooper());
    initWebSocketThread();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putDouble("longitude", longitude);
    outState.putDouble("latitude", latitude);
    if (racers != null) {
      outState.putParcelableArrayList("racers", new ArrayList<>(racers));
    }
    outState.putInt("joined", joinBuddyFinder);
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    longitude = savedInstanceState.getDouble("longitude");
    latitude = savedInstanceState.getDouble("latitude");
    this.racers = savedInstanceState.getParcelableArrayList("racers");
    joinBuddyFinder = savedInstanceState.getInt("joined");
    if (racers != null) {
      if (!racers.isEmpty()) {
        displayRacersOnMap(racers);
      }
    }
    GeoPoint currentLocation = new GeoPoint(latitude, longitude);
    mapController.setCenter(currentLocation);
  }

  public void joinBuddyFinder() {
    Bundle bundle = new Bundle();
    bundle.putDouble("latitude", latitude);
    bundle.putDouble("longitude", longitude);
    bundle.putString("alias", getNickname());
    sendCommandMessageToBFWebSocketThread(bundle, 1);
    if (!BuildConfig.DEBUG) {
      Bundle firebaseParamBundle = new Bundle();
      firebaseParamBundle.putString("alias", getNickname());
      mFirebaseAnalytics.logEvent(FirebaseEvent.FIND_BUDDY, firebaseParamBundle);
    }
  }

  private String getNickname() {
    if (TextUtils.isEmpty(nickname) || NULL_NICK_NAME.equals(nickname)) {
      return DEFAULT_NICK_NAME;
    } else {
      return nickname;
    }
  }


  private void initMap() {
    mapView.setBuiltInZoomControls(true);
    mapView.setMultiTouchControls(true);
    mapView.setUseDataConnection(true);
//TODO
    if (!AndroidUtils.isSDCardAvailable()) {
//      org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants
//          .setCachePath(getCacheDir().getAbsolutePath());
    }

    mapView.setTileSource(TileSourceFactory.MAPNIK);

    overlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
    overlay.enableMyLocation();
    mapView.getOverlays().add(overlay);
    mapController = mapView.getController();
    mapController.setZoom(13);
    GeoPoint startPoint = new GeoPoint(23.2913, 42.6636);
    mapController.setCenter(startPoint);
  }

  private void initWebSocketThread() {
    Message m = Message.obtain();
    m.what = RaceService.START_BUDDY_FINDER_ACTION;
    EventBus.getDefault().post(new BuddyFinderLifeCycleCommandEvent(m));
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void handleBuddyFinderEvent(BuddyFinderWebSocketEvent event) {
    Message inputMessage = event.getMessage();
    switch (inputMessage.what) {
      //it's used for joining the finder
      case JOINED_FINDER: {
        racers = inputMessage.getData().getParcelableArrayList("racers");
        initAvailableRacersUpdate();
        showRacers(racers, JOINED_FINDER);
        break;
      }
      case RECEIVED_MESSAGE:
        if (dialog != null) {
          if (dialog.getDialog() != null) {
            dialog.getDialog().dismiss();
            dialog = new ReceivedMessageDialogFragment();
            dialog.setArguments(inputMessage.getData());
            dialog.show(getFragmentManager(), "tag");
          } else {
            dialog.setArguments(inputMessage.getData());
            dialog.show(getFragmentManager(), "tag");
          }
        }
        break;
      //it's used for pulling up to date list of available racers
      case RECEIVER_RACERS_PULL:
        racers = inputMessage.getData().getParcelableArrayList("racers");
        showRacers(racers, RECEIVER_RACERS_PULL);
        break;
    }

  }

  private void showRacers(List<JoinFinderDTO> racers, int command) {
    if (racers != null) {
      if (!racers.isEmpty()) {
        removeAllMarkers();
        displayRacersOnMap(racers);

        //Zoom map only if there are more than one available racers
        // and the logged has just joined in the buddy finder
        if (racers.size() > 1 && command == JOINED_FINDER) {
          zoomMap(racers);
        }
      }
    }
  }

  private void initAvailableRacersUpdate() {
    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    exec.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        sendCommandMessageToBFWebSocketThread(bundle, BuddyFinderWebSocketThread.PULL_RACERS);
      }
    }, 0, 5, TimeUnit.SECONDS);
  }

  private void removeAllMarkers() {
    mapView.getOverlays().clear();
    mapView.getOverlays().add(overlay);
  }

  private void displayRacersOnMap(List<JoinFinderDTO> racers) {

    for (JoinFinderDTO racer : racers) {
      addRacerOnMap(new GeoPoint(racer.getLatitude(), racer.getLongitude()), racer);
    }
  }

  private void addRacerOnMap(GeoPoint geoPoint, final JoinFinderDTO racer) {
    Marker marker = new Marker(mapView);
    marker.setPosition(geoPoint);
    marker.setTitle(racer.getNickName());
    marker.setOnMarkerClickListener(new OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker, MapView mapView) {
        ContactRacerDialogFragment dialog = new ContactRacerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("message", racer);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "tag");
        return true;
      }
    });
    marker.setAnchor(Marker.ANCHOR_CENTER,
        Marker.ANCHOR_BOTTOM);
    mapView.getOverlays().add(marker);
    mapView.invalidate();

  }

  private void zoomMap(List<JoinFinderDTO> racers) {
    mapView.zoomToBoundingBox(getBoundingBox(racers));
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onLocationChanged(BuddyFinderLocationEvent event) {
    Location location = event.getLocation();
    GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
    latitude = location.getLatitude();
    longitude = location.getLongitude();
    mapController.setCenter(currentLocation);

    checkIfJoinedBuddyFinder();
  }

  private void checkIfJoinedBuddyFinder() {
    if (joinBuddyFinder == 3) {
      Toast.makeText(this, getResources().getString(R.string.joined), Toast.LENGTH_SHORT).show();
      joinBuddyFinder();
    }
    joinBuddyFinder++;
  }

  private void leaveBuddyFinder() {
    Message m = Message.obtain();
    m.what = RaceService.STOP_BUDDY_FINDER_ACTION;
    EventBus.getDefault().post(new BuddyFinderLifeCycleCommandEvent(m));
  }

  private void disconnectSocket() {
    sendCommandMessageToBFWebSocketThread(null, BuddyFinderWebSocketThread.DISCONNECT_SOCKET);
  }

}
