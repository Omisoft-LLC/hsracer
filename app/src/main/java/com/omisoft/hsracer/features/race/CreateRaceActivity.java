package com.omisoft.hsracer.features.race;

import static com.omisoft.hsracer.constants.Constants.CARS_LIST;
import static com.omisoft.hsracer.constants.Constants.RACE_NAME;
import static com.omisoft.hsracer.constants.Constants.REST_ID;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import butterknife.BindView;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseApp.Permissions;
import com.omisoft.hsracer.common.BaseToolbarActivity;
import com.omisoft.hsracer.common.events.ServiceCommandEvent;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.constants.FirebaseEvent;
import com.omisoft.hsracer.dto.CannonBallAddressInfoDTO;
import com.omisoft.hsracer.dto.RaceInfoDTO;
import com.omisoft.hsracer.features.race.events.RaceWebSocketEvent;
import com.omisoft.hsracer.features.race.fragments.createrace.CannonballFragment;
import com.omisoft.hsracer.features.race.fragments.createrace.DragFragment;
import com.omisoft.hsracer.features.race.fragments.createrace.TopSpeedRaceFragment;
import com.omisoft.hsracer.features.race.threads.RaceWebSocketThread;
import com.omisoft.hsracer.model.Car;
import com.omisoft.hsracer.services.RaceService;
import com.omisoft.hsracer.utils.AndroidUtils;
import com.omisoft.hsracer.ws.protocol.dto.JoinedDTO;
import com.omisoft.hsracer.ws.protocol.enums.MessageType;
import com.omisoft.hsracer.ws.protocol.enums.RaceType;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Racers create race here
 * TODO move repeated code to baseraceactivity
 * Created by Omisoft LLC. on 5/16/17.
 */
public class CreateRaceActivity extends BaseToolbarActivity {

  private static final int MINIMUM_TOTAL_NUMBER_OF_RACERS = 1;
  private static final int MAXIMUM_TOTAL_NUMBER_OF_RACERS = 8;
  private static final int ERROR_NUMBER_OF_RACERS = 100;
  public static final int CANNONBALL_MAP_OK = 101;

  private static final long CARS_NOT_FOUND = 404;

  @BindView(R.id.number_of_other_competitors)
  EditText numberCompEditText;
  @BindView(R.id.race_description)
  EditText raceDescriptionEditText;
  @BindView(R.id.race_name)
  EditText raceNameEditText;
  @BindView(R.id.video_check_box)
  CheckBox videoCheckBox;
  @BindView(R.id.video_stream_box)
  CheckBox videoStreamBox;

  @BindView(R.id.obd_check_box)
  CheckBox obdCheckBox;
  @BindView(R.id.create_race_btn)
  Button createRaceButton;
  private RaceType raceType;
  private ArrayList<Car> cars;
  private CannonBallAddressInfoDTO addressInfo;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_race);
    if (savedInstanceState == null) {
      cars = getIntent().getParcelableArrayListExtra(CARS_LIST);
    }
    setOBDEnabled();
    initSpinners(cars);
  }


  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelableArrayList(CARS_LIST, cars);
    outState.putParcelable(CannonBallAddressInfoDTO.class.getName(), addressInfo);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    cars = savedInstanceState.getParcelableArrayList(CARS_LIST);
    addressInfo = savedInstanceState.getParcelable(CannonBallAddressInfoDTO.class.getName());
    initSpinners(cars);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * Check for permissions
   */
  private void checkPreferences() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED) {
      setVideoAndPreview(true);
    } else {
      ActivityCompat.requestPermissions(this, new String[]{
          Manifest.permission.CAMERA,
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.RECORD_AUDIO
      }, CAMERA_PERMISSION_REQUEST);
    }
  }

  public void onRequestPermissionsResult(int requestCode,
      @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case CAMERA_PERMISSION_REQUEST: {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
          setVideoAndPreview(false);
          setStreamingAllowed(false);
          videoCheckBox.setChecked(false);
          videoStreamBox.setChecked(false);
          showWhiteToastMessage(getString(R.string.write_to_external_storage_permission));
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
          setVideoAndPreview(false);
          setStreamingAllowed(false);
          videoCheckBox.setChecked(false);
          videoStreamBox.setChecked(false);
          showWhiteToastMessage(getString(R.string.camera_permission));
        } else {
          if (videoCheckBox.isChecked()) {
            setVideoAndPreview(true);
          }
          if (videoStreamBox.isChecked()) {
            setStreamingAllowed(true);
          }

        }

        break;
      }
    }
  }

  /**
   * Allows to be used a camera in the race
   */
  private void setVideoAndPreview(boolean enabledVideoAndPreview) {
    SharedPreferences.Editor editor = getSharedPreferences().edit();
    editor.putBoolean(Constants.RECORD_VIDEO, enabledVideoAndPreview);
    editor.putBoolean(Constants.SHOW_PREVIEW,
        enabledVideoAndPreview);
    editor.apply();
    Permissions.Video = enabledVideoAndPreview;
  }

  /**
   * Allows streaming
   */
  private void setStreamingAllowed(boolean enableStreaming) {
    SharedPreferences.Editor editor = getSharedPreferences().edit();
    editor.putBoolean(Constants.LIVE_STREAM, enableStreaming);
    editor.putBoolean(Constants.SHOW_PREVIEW,
        enableStreaming);
    editor.apply();
    Permissions.Video = enableStreaming;
  }

  /**
   * Checks if the video and obd check boxes are checked
   */
  public void checkVideoAndOBD(View view) {
    switch (view.getId()) {
      case R.id.video_check_box:
        if (videoCheckBox.isChecked()) {
          if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
              == PackageManager.PERMISSION_GRANTED
              &&
              ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                  == PackageManager.PERMISSION_GRANTED) {
            setVideoAndPreview(true);
          } else {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            }, CAMERA_PERMISSION_REQUEST);
            setVideoAndPreview(false);
          }
        } else {
          checkPreferences();
          setVideoAndPreview(false);
        }
        break;
      case R.id.obd_check_box:
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(Constants.LIVE_OBD_DATA, obdCheckBox.isChecked());
        editor.apply();
        break;

      case R.id.video_stream_box:
        if (videoStreamBox.isChecked()) {
          if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
              == PackageManager.PERMISSION_GRANTED
              &&
              ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                  == PackageManager.PERMISSION_GRANTED) {
            setVideoAndPreview(true);
            setStreamingAllowed(true);
          } else {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            }, CAMERA_PERMISSION_REQUEST);
            setVideoAndPreview(false);
            setStreamingAllowed(false);

          }
        } else {
          checkPreferences();
          setVideoAndPreview(false);
          setStreamingAllowed(false);

        }
        break;
      default:
    }
  }


  /**
   * Initializes the race type spinner and the spinner in which are shown the created cars A
   */
  private void initSpinners(ArrayList<Car> cars) {
    initRaceTypesSpinner();
    initMyCarsSpinner(cars);
  }

  /**
   * Initializes the race spinner in which are specified the type of races which the user can
   * create. They are CANNONBAL, TOPSPEEDRACE and DRAGRACE
   */
  private void initRaceTypesSpinner() {
    String[] races = getResources().getStringArray(R.array.race_types);
    final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_row,
        R.id.text_view_row, races);
    final Spinner raceTypes = findViewById(R.id.race_type_spinner);
    raceTypes.setAdapter(spinnerAdapter);
    raceTypes.setSelection(0);
    raceTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
          case 0:
            DragFragment dragFragment = new DragFragment();
            replaceFragment(R.id.race_type_create_race_frame, dragFragment);
            raceType = RaceType.DRAG;
            break;
          case 1:
            TopSpeedRaceFragment topSpeedRaceFragment = new TopSpeedRaceFragment();
            replaceFragment(R.id.race_type_create_race_frame, topSpeedRaceFragment);
            raceType = RaceType.TOP_SPEED;
            break;
          case 2:
            CannonballFragment cannonballFragment = new CannonballFragment();
            replaceFragment(R.id.race_type_create_race_frame, cannonballFragment);
            raceType = RaceType.CANNONBALL;
            break;
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }

  /**
   * Init the spinner with the created cars till now
   */
  public void initMyCarsSpinner(final ArrayList<Car> cars) {
    final ArrayAdapter<String> spinnerAdapter;
    if (cars != null && !cars.isEmpty()) {
      Long carId = cars.get(0).getRestId();
      String[] carsArray = AndroidUtils.getCarsArray(cars);
      Editor editor = getSharedPreferences().edit();

      if (!cars.isEmpty()) {
        editor.putLong(Constants.CAR_REST_ID, carId);
        editor.apply();
      }

      spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_row,
          R.id.text_view_row, carsArray);

      final Spinner myCar = findViewById(R.id.personal_cars_create_race_spinner);
      myCar.setAdapter(spinnerAdapter);
      myCar.setSelection(0);
      myCar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          Editor editor = getSharedPreferences().edit();
          editor.putLong(Constants.CAR_REST_ID, cars.get(position).getRestId());
          editor.apply();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
      });
    }
  }


  /**
   * When the button for creating a race is clicked depending on the spinner for the race types a
   * different race is created.
   */
  public void createRaceBtnOnClickListener(View view) {
    if (getApp().isConnected()) {
      if (getApp().checkIsGPSEnabled()) {
        Message m = Message.obtain();
        m.what = RaceService.START_WEBSOCKET_ACTION;
        EventBus.getDefault().post(new ServiceCommandEvent(m));
        if (CARS_NOT_FOUND == getSharedPreferences()
            .getLong(Constants.CAR_REST_ID, CARS_NOT_FOUND)) {
          Toast.makeText(this, getResources().getString(R.string.need_car), Toast.LENGTH_SHORT)
              .show();
          return;
        }

        int totalCompetitors = MINIMUM_TOTAL_NUMBER_OF_RACERS;

        totalCompetitors = getNumberOfCompetitors();
        if (totalCompetitors > MAXIMUM_TOTAL_NUMBER_OF_RACERS) {
          showWhiteToastMessage(getString(R.string.too_many_racers));
          return;
        }
        if (totalCompetitors == 0) {
          showWhiteToastMessage(getString(R.string.not_enough_racers));
          return;
        }

        RaceInfoDTO raceInfoDTO = new RaceInfoDTO();
        switch (raceType) {
          case DRAG:
            String distanceSprint = ((EditText) findViewById(R.id.race_distance_sprint_race_frag))
                .getText().toString();
            if (TextUtils.isEmpty(distanceSprint)) {
              showWhiteToastMessage(getString(R.string.please_fill_distance));
              return;
            }
            // if distanceSprint.length() > 10 integer will be out o bounds and program will crash
            if (distanceSprint.length() <= 10) {
              SharedPreferences sharedPreferences = getSharedPreferences();
              if (sharedPreferences
                  .getString(getString(R.string.pref_metric_key), getString(R.string.metric_system))
                  .equals(getString(R.string.metric_system))) {
                raceInfoDTO.setDistance(Integer.parseInt(distanceSprint));
              } else {
                raceInfoDTO
                    .setDistance(AndroidUtils.fromFeetToMeter(Integer.parseInt(distanceSprint)));
              }
            } else {
              showWhiteToastMessage(getString(R.string.too_long_distance));
              return;
            }
            break;

          case TOP_SPEED:
            raceInfoDTO.setStartSpeed(0);
            String endSpeedRoll = ((EditText) findViewById(R.id.end_speed_race_frag)).getText()
                .toString();

            if (TextUtils.isEmpty(endSpeedRoll)) {
              showWhiteToastMessage(getString(R.string.fill_all_fields));
              return;
            }
            // if endSpeedRoll.length() > 10 integer will be out of bounds and program will crash
            if (endSpeedRoll.length() <= 3) {
              if (Integer.parseInt(endSpeedRoll) > 0) {
                SharedPreferences sharedPreferences = getSharedPreferences();
                if (sharedPreferences
                    .getString(getString(R.string.pref_metric_key),
                        getString(R.string.metric_system))
                    .equalsIgnoreCase(getString(R.string.metric_system))) {
                  raceInfoDTO.setEndSpeed(Integer.parseInt(endSpeedRoll));
                } else {
                  raceInfoDTO.setEndSpeed(
                      AndroidUtils.fromMilesToKilometres(Integer.parseInt(endSpeedRoll)));
                }
              } else {
                showWhiteToastMessage(getString(R.string.start_end_warning));
                return;
              }
            } else {
              showWhiteToastMessage(getString(R.string.end_speed_too_high));
              return;
            }
            break;
          case CANNONBALL:
            if (BuildConfig.DEBUG) {
              EditText editText = findViewById(
                  R.id.et_show_cannonbal_location_create_race);
              if (editText.getText().toString().equals(" ")) {
                showWhiteToastMessage(getString(R.string.please_choose_finish_destination));
                return;
              }
              Log.wtf(TAG, "CREATE RACE" + addressInfo.getAddress());
              raceInfoDTO.setFinishLat(addressInfo.getLatitude());
              raceInfoDTO.setFinishLng(addressInfo.getLongitude());
              raceInfoDTO.setFinishAddress(addressInfo.getAddress());
            } else {
              showWhiteToastMessage(R.string.coming_soon);
            }
            break;

        }
        setCreator();

        raceInfoDTO.setRaceType(raceType);
        raceInfoDTO.setCreatorId(getSharedPreferences().getLong(REST_ID, 0));
        raceInfoDTO.setCountRacers(totalCompetitors);
        raceInfoDTO.setName(raceNameEditText.getText().toString());
        raceInfoDTO.setDescription(raceDescriptionEditText.getText().toString());
        Bundle bundle = new Bundle();
        bundle.putParcelable(RaceInfoDTO.class.getName(), raceInfoDTO);
        sendCommandMessageToRaceWebSocketThread(bundle, RaceWebSocketThread.NEW_RACE);
      } else {
        showAlertGPS();
      }
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }

  /**
   * Set creator for this race
   */
  private void setCreator() {
    Editor editor = getSharedPreferences().edit();
    editor.putBoolean(Constants.CREATOR, true);
    editor.apply();
  }

  /**
   * Gets the number of racers specified in the field. A check for the number specified in field is
   * ran so it can be checked if the value is greater than 8 or if the field is empty
   */
  private int getNumberOfCompetitors() {
    int totalCompetitors;
    String numberOfTotalRacers = numberCompEditText.getText().toString();
    if (TextUtils.isEmpty(numberOfTotalRacers)) {
      totalCompetitors = MINIMUM_TOTAL_NUMBER_OF_RACERS;
    } else {
      if (numberOfTotalRacers.length() == 1) {
        totalCompetitors = Integer.parseInt(numberOfTotalRacers);
      } else {
        totalCompetitors = ERROR_NUMBER_OF_RACERS;
      }
    }
    return totalCompetitors;
  }

  /**
   * Enables the OBD check box if the OBD is turned on in Settings
   */
  private void setOBDEnabled() {
    if (AndroidUtils.isOBDReadReady(getSharedPreferences())) {
      obdCheckBox.setEnabled(true);
    } else {
      obdCheckBox.setEnabled(false);
    }
  }

  /**
   * Navigates the user to {@link PreRaceSummaryActivity} with the information for the  created race.
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onRaceWebSocketEvent(RaceWebSocketEvent event) {
    Message inputMessage = event.getMessage();
    MessageType t = MessageType.fromCode(inputMessage.what);
    switch (t) {
      case JOINED: {
        JoinedDTO joinedDTO = inputMessage.getData().getParcelable(JoinedDTO.class.getName());
        if (!BuildConfig.DEBUG) {
          Bundle firebaseParamBundle = new Bundle();
          firebaseParamBundle.putLong("race_id", joinedDTO.getRaceId());
          mFirebaseAnalytics.logEvent(FirebaseEvent.CREATE_RACE, firebaseParamBundle);
        }
        Intent intent = new Intent(CreateRaceActivity.this, PreRaceSummaryActivity.class);
        intent.putExtra(RACE_NAME, raceNameEditText.getText().toString());
        intent.putExtra(JoinedDTO.class.getName(), joinedDTO);
        startActivity(intent);
        break;
      }
    }
  }

  /**
   * Called when the  in {@link CannonballMapActivity} is choosen a location
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case CANNONBALL_MAP_OK:
        if (resultCode == RESULT_OK) {
          Bundle bundle = data.getExtras();
          if (bundle != null) {
            this.addressInfo = bundle.getParcelable(CannonBallAddressInfoDTO.class.getName());
            if (addressInfo.getAddress() == null) {
              EditText editText = findViewById(
                  R.id.et_show_cannonbal_location_create_race);
              editText.setText(" ");
            } else {
              EditText editText = findViewById(
                  R.id.et_show_cannonbal_location_create_race);
              editText.setText(addressInfo.getAddress());
            }
          }
        }
        break;
    }
  }
}