package com.omisoft.hsracer.features.home;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.omisoft.hsracer.constants.Constants.CARS_LIST;
import static com.omisoft.hsracer.constants.Constants.RACER_CREATES_RACE;
import static com.omisoft.hsracer.constants.Constants.RACER_JOINS_RACE;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import com.kobakei.ratethisapp.RateThisApp;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseApp.Permissions;
import com.omisoft.hsracer.common.BaseToolbarActivity;
import com.omisoft.hsracer.common.events.MockLocationEvent;
import com.omisoft.hsracer.common.events.ServiceCommandEvent;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.constants.FirebaseEvent;
import com.omisoft.hsracer.constants.URLConstants;
import com.omisoft.hsracer.debug.DebugActivity;
import com.omisoft.hsracer.features.buddyfinder.features.BuddyFinderActivity;
import com.omisoft.hsracer.features.login.LoginActivity;
import com.omisoft.hsracer.features.profile.CarsActivity;
import com.omisoft.hsracer.features.profile.ProfileActivity;
import com.omisoft.hsracer.features.profile.actions.ListCarsDbArrayListAction;
import com.omisoft.hsracer.features.profile.events.ListCarsArrayListCreateRaceEvent;
import com.omisoft.hsracer.features.profile.events.ListCarsArrayListJoinRaceEvent;
import com.omisoft.hsracer.features.race.CreateRaceActivity;
import com.omisoft.hsracer.features.race.JoinRaceActivity;
import com.omisoft.hsracer.features.race.events.OBDConnectionErrorEvent;
import com.omisoft.hsracer.features.results.ResultsActivity;
import com.omisoft.hsracer.features.settings.SettingsActivity;
import com.omisoft.hsracer.debug.stream.StreamActivity;
import com.omisoft.hsracer.features.streams.ListLiveStreamsActivity;
import com.omisoft.hsracer.model.Car;
import com.omisoft.hsracer.services.RaceService;
import com.omisoft.hsracer.utils.AndroidUtils;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Home screen activity
 * Created by Omisoft LLC. on 4/21/17.
 */

public class HomeActivity extends BaseToolbarActivity implements
    NavigationView.OnNavigationItemSelectedListener {

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.drawer_layout)
  DrawerLayout drawerLayout;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getApp().startUploadVideoService();
    getApp().clearRaceSharedPreferences();
    setContentView(R.layout.activity_home);
    setSupportActionBar(toolbar);
    drawerLayout = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        HomeActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open,
        R.string.navigation_drawer_close);
    drawerLayout.setDrawerListener(toggle);

    toggle.syncState();
    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    init();
    android.app.FragmentTransaction tx = getFragmentManager().beginTransaction();
    tx.replace(R.id.content_frame, new HomeFragment());
    tx.commit();

    RateThisApp.onCreate(this);
    RateThisApp.showRateDialogIfNeeded(this);
    View headerView = navigationView.getHeaderView(0);

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    TextView nameDrawer = headerView.findViewById(R.id.name_drawer);
    nameDrawer.setText(sharedPreferences.getString(Constants.NICK_NAME, ""));

    TextView emailDrawer = headerView.findViewById(R.id.email_drawer);
    emailDrawer.setText(sharedPreferences.getString(Constants.USER_EMAIL, ""));

    if (BuildConfig.DEBUG) {
      TextView buildNumberDrawer = headerView.findViewById(R.id.build_number_drawer);
      buildNumberDrawer.setText(BuildConfig.REVISION);
      TextView buildDateDrawer = headerView.findViewById(R.id.build_date_drawer);
      buildDateDrawer.setText(BuildConfig.BUILD_DATE);
    } else {
      headerView.findViewById(R.id.build_number_drawer).setVisibility(View.GONE);
      headerView.findViewById(R.id.build_date_drawer).setVisibility(View.GONE);

    }
  }

  @Override
  public void onResume() {
    super.onResume();
    checkOBDAvailability();
    isMockDisabled();
  }

  private void init() {
    String[] navigationArray = getResources().getStringArray(R.array.navigation);
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    navigationArray[0] = sharedPreferences.getString(Constants.EMAIL, "none");

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      checkPreferences();
    } else {
      if (!getApp().checkIsGPSEnabled()) {
        showAlertGPS();
      }
      startRaceService();
      Permissions.ALL_PERMISSIONS = true;
      setVideoAndPreview(true);
    }
  }


  /**
   * Logs user out
   */
  public void logoutAction() {
    View linearLayout = getLayoutInflater().inflate(R.layout.fragment_navigate_back_warning, null);
    ((TextView) linearLayout.findViewById(R.id.message1)).setText(R.string.logout_from_app);
    linearLayout.findViewById(R.id.message2).setVisibility(View.GONE);
    linearLayout.findViewById(R.id.message3).setVisibility(View.GONE);
    AlertDialog.Builder logoutAlert = new AlertDialog.Builder(this);
    logoutAlert.setTitle(R.string.logout).setView(linearLayout).setPositiveButton(R.string.logout,
        new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            removeSharedPreferences();
            Intent login = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
          }
        }).setNegativeButton(R.string.cancel, new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
      }
    });
    logoutAlert.show();
  }

  /**
   * exits app
   */
  public void exitAction() {
    stopService(new Intent(HomeActivity.this, RaceService.class));
    finishAffinity();
  }

  /**
   * Method to clear sharedPreferences' saved values
   */
  private void removeSharedPreferences() {
    SharedPreferences sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = sharedPreferences.edit().clear();
    editor.apply();
  }

  /**
   * Checks available cars for create race button
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void checkAvailableCarsForCreateRace(ListCarsArrayListCreateRaceEvent event) {
    List<Car> cars = event.getCars();
    if (cars != null && !cars.isEmpty()) {
      Intent intent = new Intent(HomeActivity.this, CreateRaceActivity.class);
      intent.putParcelableArrayListExtra(CARS_LIST, (ArrayList<? extends Parcelable>) cars);
      startActivity(intent);
    } else {
      Toast.makeText(HomeActivity.this, getResources().getString(R.string.need_car),
          Toast.LENGTH_SHORT).show();
      Intent carsIntent = new Intent(HomeActivity.this, CarsActivity.class);
      startActivity(carsIntent);
    }
  }

  /**
   * Checks available cars for create race button
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void checkAvailableCarsForJoinRace(ListCarsArrayListJoinRaceEvent event) {
    List<Car> cars = event.getCars();
    if (cars != null && !cars.isEmpty()) {
      Intent intent = new Intent(HomeActivity.this, JoinRaceActivity.class);
      intent.putParcelableArrayListExtra(CARS_LIST, (ArrayList<? extends Parcelable>) cars);
      startActivity(intent);
    } else {
      Toast.makeText(HomeActivity.this, getResources().getString(R.string.need_car),
          Toast.LENGTH_SHORT).show();
      Intent carsIntent = new Intent(HomeActivity.this, CarsActivity.class);
      startActivity(carsIntent);
    }
  }

  /**
   * Create race button listener
   **/

  public void createRaceHomeBtnOnClickListener(View view) {
    if (getApp().isConnected()) {
      if (getApp().checkIsGPSEnabled()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          checkPreferences();
        }
        if (Permissions.ALL_PERMISSIONS) {
          getExecutor()
              .submit(new ListCarsDbArrayListAction(getApp(), RACER_CREATES_RACE));
        }
      } else {
        showAlertGPS();
      }
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }


  /**
   * Join race button listener
   */
  public void joinRaceHomeBtnOnClickListener(View view) {
    if (getApp().isConnected()) {
      if (getApp().checkIsGPSEnabled()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          checkPreferences();
        }
        if (Permissions.ALL_PERMISSIONS) {
          getExecutor()
              .submit(new ListCarsDbArrayListAction(getApp(), RACER_JOINS_RACE));
        }
      } else {
        showAlertGPS();
      }
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }

  /**
   * Result race button listener.
   */
  public void resultsBtnOnClickListener(View view) {
    if (getApp().isConnected()) {
      Intent intent = new Intent(HomeActivity.this, ResultsActivity.class);
      startActivity(intent);
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }

  /**
   * Here will be implemented the function for streaming.
   */
  public void streamingBtnOnClickListener(View view) {
    startActivity(new Intent(this, ListLiveStreamsActivity.class));

  }

  private void checkPreferences() {

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        == PERMISSION_GRANTED
        ) {
      startRaceService();
      Permissions.ALL_PERMISSIONS = true;
      setVideoAndPreview(true);
      if (!getApp().checkIsGPSEnabled()) {
        showAlertGPS();
      }
    } else {
      Permissions.ALL_PERMISSIONS = false;
      ActivityCompat.requestPermissions(this, new String[]{
          Manifest.permission.CAMERA,
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.RECORD_AUDIO,
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION,
      }, CAMERA_PERMISSION_REQUEST | GPS_PERMISSION_REQUEST);
    }
  }

  /**
   * \If the video is allowed to be
   * @param enabledVideoAndPreview
   */
  private void setVideoAndPreview(boolean enabledVideoAndPreview) {
    SharedPreferences.Editor editor = getSharedPreferences().edit();
    editor.putBoolean(Constants.RECORD_VIDEO, enabledVideoAndPreview);
    editor.putBoolean(Constants.SHOW_PREVIEW,
        enabledVideoAndPreview);
    editor.apply();
    Permissions.Video = enabledVideoAndPreview;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
      @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case GPS_PERMISSION_REQUEST: {
        // If request is cancelled, the result arrays are empty.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
          showWhiteToastMessage(getString(R.string.hsracer_requires_gps));
        } else if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
          showWhiteToastMessage(getString(R.string.hsracer_requires_gps));
        }
        break;
      }

      case CAMERA_PERMISSION_REQUEST: {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
          setVideoAndPreview(false);
          showWhiteToastMessage(getString(R.string.write_to_external_storage_permission));
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
          setVideoAndPreview(false);
          showWhiteToastMessage(getString(R.string.camera_permission));
        }
        break;
      }
    }
  }


  private void startBuddyFinder() {
    Permissions.WriteToMemory = true;
    startBuddyFinderActivity();
  }

  /**
   * Starts the BuddyFinderActivity {@link BuddyFinderActivity}
   */
  private void startBuddyFinderActivity() {
    if (getApp().isConnected()) {
      if (getApp().checkIsGPSEnabled()) {
        Intent bfIntent = new Intent(HomeActivity.this, BuddyFinderActivity.class);
        startActivity(bfIntent);
      } else {
        showAlertGPS();
      }
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }

  private void startOBD() {
    Message m = Message.obtain();
    m.what = RaceService.START_OBD_ACTION;
    EventBus.getDefault().post(new ServiceCommandEvent(m));
    if (!BuildConfig.DEBUG) {
      Bundle firebaseParamBundle = new Bundle();
      mFirebaseAnalytics.logEvent(FirebaseEvent.LOG_OBD, firebaseParamBundle);
    }
  }

  /**
   * Stops the OBD
   */
  private void stopOBD() {
    Message OBDError = Message.obtain();
    /* Eventbus posts to RaceService class, OBDErrorEvent method */
    EventBus.getDefault().post(new OBDConnectionErrorEvent(OBDError));
  }

  /**
   * Checks if there can be established OBD
   */
  private void checkOBDAvailability() {
    if (AndroidUtils.isOBDReadReady(getSharedPreferences())) {
      startOBD();
    } else {
      stopOBD();
    }
  }

  /**
   * Starts Buddy Finder's lifecycle thread
   */
  private void startBuddyFinderLifeCycleThread() {
    Message m = Message.obtain();
    m.what = RaceService.START_BUDDY_FINDER_LIFECYCLE_ACTION;
    EventBus.getDefault().post(new ServiceCommandEvent(m));
  }

  /**
   * That is the menu in the drawer
   * @param item
   * @return
   */
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int id = item.getItemId();
    drawerLayout.closeDrawer(GravityCompat.START);
    switch (id) {
      case R.id.nav_home:
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.content_frame, new HomeFragment())
            .commit();
        break;
      case R.id.nav_profile:
        Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
        break;
      case R.id.nav_cars:
        item.setChecked(false);
        Intent carsIntent = new Intent(HomeActivity.this, CarsActivity.class);
        startActivity(carsIntent);
        break;
      case R.id.nav_buddyfinder:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          checkPreferences();
        }
        if (Permissions.ALL_PERMISSIONS) {
          startBuddyFinderLifeCycleThread();
          startBuddyFinder();
        }
        break;
      case R.id.nav_settings:
        Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
        break;
      case R.id.nav_help:
        String url = URLConstants.HELP_URL;
        Intent helpIntent = new Intent(Intent.ACTION_VIEW);
        helpIntent.setData(Uri.parse(url));
        startActivity(helpIntent);
        break;
      case R.id.nav_website:
        String websiteUrl = URLConstants.WEBSITE_URL;
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
        websiteIntent.setData(Uri.parse(websiteUrl));
        startActivity(websiteIntent);
        break;
      case R.id.nav_logout:
        logoutAction();
        break;

      case R.id.nav_exit:
        exitAction();
        break;

      case R.id.nav_stream_debug:
        Intent streamIntent = new Intent(HomeActivity.this, StreamActivity.class);
        startActivity(streamIntent);
        break;
      case R.id.nav_activity_debug:
        Intent debugIntent = new Intent(HomeActivity.this, DebugActivity.class);
        startActivity(debugIntent);
        break;
      default:

    }
    return true;
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

  }

  @Override
  public void onBackPressed() {
    if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.openDrawer(GravityCompat.START);
    } else {
      drawerLayout.closeDrawer(GravityCompat.START);
    }
  }

  /**
   * Check if the mock location is enabled
   */
  private void isMockDisabled() {
    char disableCheck = 'd';
    EventBus.getDefault().post(new MockLocationEvent(disableCheck));
  }
}