package com.omisoft.hsracer.features.profile;

import static com.omisoft.hsracer.model.Car.DEFAULT_BHP;
import static com.omisoft.hsracer.model.Car.DEFAULT_VOLUME;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.BindView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseToolbarActivity;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.CarDTO;
import com.omisoft.hsracer.features.profile.actions.CreateCarServerAction;
import com.omisoft.hsracer.features.profile.actions.GetMakeByTypeDbAction;
import com.omisoft.hsracer.features.profile.actions.GetModelsByMakeDbAction;
import com.omisoft.hsracer.features.profile.actions.SaveCarInDbAction;
import com.omisoft.hsracer.features.profile.events.FailedToCreateCarEvent;
import com.omisoft.hsracer.features.profile.events.ListOfModelsByMakeEvent;
import com.omisoft.hsracer.features.profile.events.ListVehicleTypeEvent;
import com.omisoft.hsracer.features.profile.events.SuccessCreateCarEvent;
import com.omisoft.hsracer.features.profile.events.SuccessfulDBSaveCarEvent;
import com.omisoft.hsracer.model.Car;
import com.omisoft.hsracer.model.FuelType;
import com.omisoft.hsracer.model.VehicleType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Adding a carName to the db
 * Created by Omisoft LLC. on 4/27/17.
 */

public class SaveCarToProfileActivity extends BaseToolbarActivity {


  private static final String FUEL_TYPE = "FUEL_TYPE";

  @BindView(R.id.type_of_vehicle)
  Spinner mTypeOfVehicleSpinner;
  @BindView(R.id.car_model_spinner)
  Spinner mModelSpinner;
  @BindView(R.id.car_make_spinner)
  Spinner mMakeSpinner;
  @BindView(R.id.fuel_types_spinner)
  Spinner mFuelTypeSpinner;
  @BindView(R.id.year_car)
  Spinner mYearSpinner;
  @BindView(R.id.engine_cylinders_car)
  Spinner mEngineCylindersSpinner;
  @BindView(R.id.alias_car)
  EditText mAliasEditText;
  @BindView(R.id.engine_volume_car)
  EditText mEngineVolumeEditText;
  @BindView(R.id.bhp_car)
  EditText mBHPEditText;
  @BindView(R.id.add_car)
  Button mAddCarButton;

  private ArrayAdapter<String> mMakesAdapter;
  private ArrayAdapter<String> mModelsAdapter;
  private ArrayAdapter<Integer> mYearAdapter;
  private ArrayAdapter<Integer> mEngineCylindersAdapter;
  private String[] mVehicles = new String[]{"cars", "motorcycles"};
  private FuelType fuelType;
  private List<Integer> mYearsList = new ArrayList<>();
  private List<Integer> mCylindersList = new ArrayList<>();
  private String mMakeItemSelected;
  private String mModelItemSelected;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_save_car_to_profile);
    initLists();
    initSpinners();
    initFuelTypeSpinner();
    initYearSpinner();
    initEngineCylindersSpinner();
  }

  /**
   * Populates the the year and cylinder spinners
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

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home: {
        onBackPressed();
        return true;
      }
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Initialize all the spinners from the layout including the spinner for type of vehicle {@code
   * mVehicles}, makes of cars and models of cars. Here are updated the values of {@code
   * mMakeItemSelected} and {@code mModelItemSelected} in which will be stored the last clicked
   * String for each of the spinners
   */
  private void initSpinners() {

    //Spinner for Type of vehicles
    ArrayAdapter<String> mVehicleAdapter = new ArrayAdapter<>(this, R.layout.spinner_row,
        R.id.text_view_row,
        mVehicles);
    mTypeOfVehicleSpinner.setAdapter(mVehicleAdapter);
    mTypeOfVehicleSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mModelsAdapter.clear();
        mModelsAdapter.add(getString(R.string.warning_please_select_make));
        mModelsAdapter.notifyDataSetChanged();
        mMakesAdapter.clear();
        mMakesAdapter.add(getString(R.string.warning_please_select_make));
        mMakesAdapter.notifyDataSetChanged();
        switch (String.valueOf(parent.getSelectedItem())) {
          case "cars":
            getExecutor().submit(new GetMakeByTypeDbAction(getApp(), "c"));
            break;
          case "motorcycles":
            getExecutor().submit(new GetMakeByTypeDbAction(getApp(), "m"));
            break;
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    //Spinner for Make vehicle
    mMakesAdapter = new ArrayAdapter<>(this, R.layout.spinner_row, R.id.text_view_row);
    mMakeSpinner.setAdapter(mMakesAdapter);
    mMakeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mMakeItemSelected = String.valueOf(adapterView.getSelectedItem());
        switch (mTypeOfVehicleSpinner.getSelectedItemPosition()) {
          case 0:
            getExecutor().submit(
                new GetModelsByMakeDbAction(getApp(), mMakeItemSelected,
                    "c"));
            break;
          case 1:
            getExecutor().submit(
                new GetModelsByMakeDbAction(getApp(), mMakeItemSelected,
                    "m"));
            break;
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });

// Spinner for Model of vehicle
    mModelsAdapter = new ArrayAdapter<>(this, R.layout.spinner_row, R.id.text_view_row);
    mModelSpinner.setAdapter(mModelsAdapter);
    mModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mModelItemSelected = String.valueOf(adapterView.getSelectedItem());
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
  }

  /**
   * Here is initialized the spinner for the fuel Type
   */
  private void initFuelTypeSpinner() {
    String[] fuelTypes = getResources().getStringArray(R.array.fuel_types);
    ArrayAdapter<String> mFuelsAdapter = new ArrayAdapter<>(this, R.layout.spinner_row,
        R.id.text_view_row, fuelTypes);
    mFuelTypeSpinner.setAdapter(mFuelsAdapter);
    mFuelTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
          case 0:
            fuelType = FuelType.PETROL;
            break;

          case 1:
            fuelType = FuelType.DIESEL;
            break;

          case 2:
            fuelType = FuelType.LPG;
            break;
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }

  /**
   * \
   * Here is initialized the spinner for number of cylinders in one car
   */
  private void initEngineCylindersSpinner() {
    mEngineCylindersAdapter = new ArrayAdapter<>(this, R.layout.spinner_row,
        R.id.text_view_row,
        mCylindersList);
    mEngineCylindersSpinner.setAdapter(mEngineCylindersAdapter);
  }

  /**
   * Here is initialized the spinner for the year of manufacturing
   */
  private void initYearSpinner() {
    mYearAdapter = new ArrayAdapter<>(this, R.layout.spinner_row,
        R.id.text_view_row,
        mYearsList);
    mYearSpinner.setAdapter(mYearAdapter);
    mYearSpinner.setSelection(50);
  }


  /**
   * Here we push the information to the server and DB regarding the information that the used
   * specified in the fields and spinner
   */
  public void addCarBtnOnClickListener(View view) {
    if (getApp().isConnected()) {

      if (mMakeItemSelected.equals(getString(R.string.warning_please_select_make))) {
        showWhiteToastMessage(getString(R.string.warning_please_select_make));
        return;
      }

      if (mModelItemSelected.equals(getString(R.string.warning_please_select_make))
          || mModelItemSelected.equals(getString(R.string.warning_please_select_model))) {
        showWhiteToastMessage(getString(R.string.warning_please_select_model));
        return;
      }

      Integer bhp = DEFAULT_BHP;
      String bhpText = mBHPEditText.getText().toString();
      String volumeText = mEngineVolumeEditText.getText().toString();

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
      newCarDTO.setAlias(mAliasEditText.getText().toString());
      newCarDTO.setMake(mMakeItemSelected);
      newCarDTO.setModel(mModelItemSelected);
      newCarDTO.setYear(
          String.valueOf(mYearAdapter.getItem(mYearSpinner.getSelectedItemPosition())));
      newCarDTO.setEngineCylinders(String.valueOf(
          mEngineCylindersAdapter.getItem(mEngineCylindersSpinner.getSelectedItemPosition())));
      newCarDTO.setVolume(volumeText);
      newCarDTO.setHpr(bhp);
      newCarDTO.setFuel(fuelType);
      newCarDTO.setVehicleType(
          (mTypeOfVehicleSpinner.getSelectedItemPosition() == 0 ? VehicleType.c : VehicleType.m));
      getApp().getExecutor()
          .submit(new CreateCarServerAction(getApp(), newCarDTO));
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }

  /**
   * Updating the spinner for makes regardless of which vehicle is chosen. Send by {@link GetMakeByTypeDbAction}
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void populateMakesInSpinnerMake(ListVehicleTypeEvent e) {
    mMakesAdapter.clear();
    mMakesAdapter.add(getString(R.string.warning_please_select_make));
    mMakesAdapter.notifyDataSetChanged();
    mMakesAdapter.addAll(e.getVehicles());
    mMakesAdapter.notifyDataSetChanged();
  }

  /**
   * Updating the spinner for models regardless of which make is chosen. The spinner wont be updated
   * until a make is chosen from {@code mModelSpinner}
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void populateModelsInSpinnerModel(ListOfModelsByMakeEvent e) {
    mModelsAdapter.clear();
    if (e.getModels().size() == 0) {
      mModelsAdapter.add(getString(R.string.warning_please_select_make));
    } else {
      mModelsAdapter.add(getString(R.string.warning_please_select_model));
      mModelsAdapter.addAll(e.getModels());
    }
    mModelsAdapter.notifyDataSetChanged();
  }

  /**
   * If the server is down or when you can not reach the server
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void failedToCreateCar(FailedToCreateCarEvent event) {
    showWhiteToastMessage(event.getMessage());
  }

  /**
   * After a success server action on creating the car this method is called and a new car is
   * created so it can be saved to the local DB. Send by {@link SuccessCreateCarEvent}
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void successfulServerSave(SuccessCreateCarEvent event) {
    Car car = new Car();
    car.setManufacturer(mMakeItemSelected);
    car.setModel(mModelItemSelected);
    car.setAlias(mAliasEditText.getText().toString());
    car.setYear(mYearAdapter.getItem(mYearSpinner.getSelectedItemPosition()));
    car.setProfileId(getSharedPreferences().getLong(Constants.ID, 10000));
    car.setRestId(event.getRestId());
    car.setVolume(
        (!TextUtils.isEmpty(mEngineVolumeEditText.getText()) ? mEngineVolumeEditText.getText()
            .toString() : "0"));
    car.setEngineCylinders(String.valueOf(String.valueOf(
        mEngineCylindersAdapter.getItem(mEngineCylindersSpinner.getSelectedItemPosition()))));
    car.setHpr((!TextUtils.isEmpty(mBHPEditText.getText()) ? Integer
        .valueOf(mBHPEditText.getText().toString()) : 0));
    car.setVehicleType(
        (mTypeOfVehicleSpinner.getSelectedItemPosition() == 0 ? VehicleType.c : VehicleType.m));
    car.setFuel(fuelType);
    car.setProfileId(getSharedPreferences().getLong(Constants.ID, 0));
    getExecutor().submit(new SaveCarInDbAction(car, getApp()));
  }

  /**
   * Called if there is success in saving the car into the DB. Send by {@link SaveCarInDbAction}
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void successfulDBSave(SuccessfulDBSaveCarEvent e) {
    finish();
  }
}
