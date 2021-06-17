package com.omisoft.hsracer.features.profile;

import static com.omisoft.hsracer.constants.Constants.CAR_EDIT;
import static com.omisoft.hsracer.constants.Constants.LOCAL_CAR_ID;
import static com.omisoft.hsracer.model.Car.DEFAULT_BHP;
import static com.omisoft.hsracer.model.Car.DEFAULT_VOLUME;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.BindView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.CarDTO;
import com.omisoft.hsracer.features.profile.actions.UpdateCarDbAction;
import com.omisoft.hsracer.features.profile.actions.UpdateCarServerAction;
import com.omisoft.hsracer.features.profile.events.FailedToUpdateCarEvent;
import com.omisoft.hsracer.features.profile.events.SuccessUpdateCarEvent;
import com.omisoft.hsracer.features.profile.events.SuccessfulDBSaveCarEvent;
import com.omisoft.hsracer.model.Car;
import com.omisoft.hsracer.model.FuelType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Edit Car Activity
 * Created by developer on 11.12.17.
 */

public class EditCarInfoActivity extends BaseActivity {

  @BindView(R.id.year_car_edit)
  Spinner mYearSpinner;
  @BindView(R.id.engine_cylinders_car_edit)
  Spinner mEngineCylindersSpinners;
  @BindView(R.id.fuel_types_spinner_edit)
  Spinner mFuelSpinner;
  @BindView(R.id.bhp_car_edit)
  EditText mBHP;
  @BindView(R.id.engine_volume_car_edit)
  EditText mEngineVolume;

  private Car car;
  private ArrayAdapter<Integer> mYearAdapter;
  private ArrayAdapter<Integer> mEngineCylindersAdapter;
  private ArrayAdapter<String> mFuelAdapter;
  private List<Integer> mYearsList = new ArrayList<>();
  private List<Integer> mCylindersList = new ArrayList<>();

  private long idCar;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_editing_car_details);
    if (getIntent().hasExtra(CAR_EDIT)) {
      car = getIntent().getExtras().getParcelable(CAR_EDIT);
      idCar = getIntent().getExtras().getLong(LOCAL_CAR_ID);
      initLists();
      initSpinnersAndViews();
    }
  }

  /**
   * Init all the spinners and views in the activity with the information from {@link
   * DetailsCarActivity}
   */
  private void initSpinnersAndViews() {
    mYearAdapter = new ArrayAdapter<Integer>(EditCarInfoActivity.this, R.layout.spinner_row,
        R.id.text_view_row, mYearsList);
    mYearSpinner.setAdapter(mYearAdapter);
    mYearSpinner.setSelection(mYearAdapter.getPosition(car.getYear()));

    mEngineCylindersAdapter = new ArrayAdapter<>(EditCarInfoActivity.this,
        R.layout.spinner_row, R.id.text_view_row, mCylindersList);
    mEngineCylindersSpinners.setAdapter(mEngineCylindersAdapter);
    mEngineCylindersSpinners.setSelection(mEngineCylindersAdapter.getPosition(
        Integer.valueOf(car.getEngineCylinders())));

    mFuelAdapter = new ArrayAdapter<>(EditCarInfoActivity.this, R.layout.spinner_row,
        R.id.text_view_row, getResources().getStringArray(R.array.fuel_types));
    mFuelSpinner.setAdapter(mFuelAdapter);
    mFuelSpinner.setSelection(mFuelAdapter.getPosition(car.getFuel().name()));

    mEngineVolume.setText(car.getVolume());

    mBHP.setText(String.valueOf(car.getHpr()));
  }

  /**
   * Populates the year and cylinder spinners
   */
  private void initLists() {
    int STARTING_YEAR = 1950;
    for (int i = STARTING_YEAR; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
      mYearsList.add(i);
    }
    int MIN_CYLINDERS = 2;
    int MAX_CYLINDERS = 16;
    for (int i = MIN_CYLINDERS; i <= MAX_CYLINDERS; i++) {
      mCylindersList.add(i);
    }
  }

  /**
   * Returns the user back to {@link DetailsCarActivity}
   */
  public void returnToCarDetails(View view) {
    onBackPressed();
  }

  /**
   * When the button Save is clicked a request to the server (by the thread {@link UpdateCarServerAction} }is send so the information about the
   * new info for the car to be updated
   */
  public void updateInfoAboutCar(View view) {
    if (getApp().isConnected()) {

      Integer bhp = DEFAULT_BHP;
      String bhpText = mBHP.getText().toString();
      String volumeText = mEngineVolume.getText().toString();

      if (!TextUtils.isEmpty(bhpText)) {
        if (bhpText.length() > 4) {
          showWhiteToastMessage("Invalid BHP");
          return;
        } else {
          bhp = Integer.parseInt(bhpText);
        }
      }

      if (!TextUtils.isEmpty(volumeText)) {
        if (volumeText.length() > 4) {
          showWhiteToastMessage("Invalid volume");
          return;
        }
      } else {
        volumeText = DEFAULT_VOLUME;
      }

      CarDTO newCarDTO = new CarDTO();
      newCarDTO.setRestId(car.getRestId());
      newCarDTO.setAlias(car.getAlias());
      newCarDTO.setMake(car.getManufacturer());
      newCarDTO.setModel(car.getModel());
      newCarDTO.setYear(
          String.valueOf(mYearAdapter.getItem(mYearSpinner.getSelectedItemPosition())));
      newCarDTO.setEngineCylinders(String.valueOf(
          mEngineCylindersAdapter.getItem(mEngineCylindersSpinners.getSelectedItemPosition())));
      newCarDTO.setVolume(volumeText);
      newCarDTO.setHpr(bhp);
      newCarDTO
          .setFuel(FuelType.valueOf(mFuelAdapter.getItem(mFuelSpinner.getSelectedItemPosition())));
      newCarDTO.setVehicleType(
          (car.getVehicleType()));
      getApp().getExecutor()
          .submit(new UpdateCarServerAction(getApp(), newCarDTO));
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }

  /**
   * After success updating on the server that method is called and the new information is specified
   * in Car and then updated in the data base. Send  by {@link UpdateCarServerAction}
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void successUpdatingOfCarServer(SuccessUpdateCarEvent e) {
    car.setId(idCar);
    car.setYear(mYearAdapter.getItem(mYearSpinner.getSelectedItemPosition()));
    car.setEngineCylinders(String.valueOf(
        mEngineCylindersAdapter.getItem(mEngineCylindersSpinners.getSelectedItemPosition())));
    car.setVolume(!TextUtils.isEmpty(mEngineVolume.getText()) ? mEngineVolume.getText()
        .toString() : "0");
    car.setHpr(
        (!TextUtils.isEmpty(mBHP.getText()) ? Integer.valueOf(mBHP.getText().toString()) : 0));
    car.setFuel(FuelType.valueOf(mFuelAdapter.getItem(mFuelSpinner.getSelectedItemPosition())));
    getExecutor().submit(new UpdateCarDbAction(getApp(), car));
  }

  /**
   * After a success update from form {@link UpdateCarServerAction} this method is called and it
   * returns the user to {@link DetailsCarActivity} where the new information is updated
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void successUpdatingFfCarDB(SuccessfulDBSaveCarEvent e) {
    Intent intent = new Intent();
    intent.putExtra(Constants.CAR_EDIT, car);
    setResult(RESULT_OK, intent);
    hideSoftKeyboard();
    finish();
  }

  /**
   * That method is called form {@link UpdateCarServerAction} when a server error occured. The user
   * will be navigated back to the {@link DetailsCarActivity}.
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void FailedToUpdateCarToSever(FailedToUpdateCarEvent event) {
    Intent intent = new Intent();
    intent.putExtra(Constants.CAR_EDIT, event.getMessage());
    setResult(RESULT_CANCELED, intent);
    hideSoftKeyboard();
    finish();
  }
}
