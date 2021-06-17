package com.omisoft.hsracer.features.profile;

import static com.omisoft.hsracer.constants.Constants.LOCAL_CAR_ID;
import static com.omisoft.hsracer.constants.Constants.SELECTED_CAR;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import butterknife.BindView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.statistics.PositionStatDTO;
import com.omisoft.hsracer.model.Car;

/**
 * Car details
 * Created by developer on 08.12.17.
 */

public class DetailsCarActivity extends BaseActivity {

  private final static int UPDATING = 200;
  @BindView(R.id.details_car_name)
  TextView mCarNameTextView;
  @BindView(R.id.details_car_make_and_model)
  TextView mCarMakeAndModel;
  @BindView(R.id.car_details_year)
  TextView mCarYearOfManifacture;
  @BindView(R.id.car_details_engine_cylinders)
  TextView mCarEngineCylinders;
  @BindView(R.id.car_details_engine_volume)
  TextView mCarVolume;
  @BindView(R.id.car_details_bhp)
  TextView mCarBHP;
  @BindView(R.id.car_details_fuel)
  TextView mCarFuel;
  @BindView(R.id.ww_pie_chart)
  WebView mPieChartWebView;
  private Car car;
  private long idCar;

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details_car_from_profile);
    Toolbar toolbar = findViewById(R.id.car_details_toolbar);
    toolbar.setTitle("");
    setSupportActionBar(toolbar);
    @SuppressLint("PrivateResource") final Drawable upArrow = getResources()
        .getDrawable(R.drawable.ic_arrow_left);
    upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(upArrow);
    if (getIntent().hasExtra(SELECTED_CAR)) {
      car = getIntent().getParcelableExtra(SELECTED_CAR);
      idCar = getIntent().getLongExtra(LOCAL_CAR_ID, 0);
      mCarNameTextView.setText(car.getAlias());
      mCarMakeAndModel.setText(String.format("%s - %s", car.getManufacturer(), car.getModel()));
      mCarYearOfManifacture.setText(String.valueOf(car.getYear()));
      mCarEngineCylinders.setText(car.getEngineCylinders());
      mCarVolume.setText(car.getVolume());
      mCarBHP.setText(String.valueOf(car.getHpr()));
      mCarFuel.setText(car.getFuel().toString());
      PositionStatDTO dto = getIntent().getParcelableExtra(PositionStatDTO.class.getName());
      mPieChartWebView.getSettings().setJavaScriptEnabled(true);
      mPieChartWebView
          .addJavascriptInterface(new JavascriptStatistics(dto, getObjectMapper()), "Android");
      mPieChartWebView.loadUrl("file:///android_asset/charts/charts.html");
    }
  }

  /**
   * Here a specified the buttons from the toolbar. There are 2 buttons. The first is fro navigating
   * back and the second is fro updating the information for the car specified int hat activity
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    int itemSelected = item.getItemId();

    switch (itemSelected) {
      case android.R.id.home:
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
          finishAfterTransition();
        } else {
          onBackPressed();
        }
        return true;
      case R.id.car_update_menu_item:
        Intent intent = new Intent(DetailsCarActivity.this, EditCarInfoActivity.class);
        intent.putExtra(Constants.CAR_EDIT, car);
        intent.putExtra(Constants.LOCAL_CAR_ID, idCar);
        startActivityForResult(intent, UPDATING);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_car_details, menu);
    return true;
  }

  /**
   * Updates the car when the fields from {@link EditCarInfoActivity} are changed and the save
   * button is pressed.
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == UPDATING) {
      switch (resultCode) {
        case RESULT_OK:
          car = data.getExtras().getParcelable(Constants.CAR_EDIT);
          if (car != null) {
            mCarYearOfManifacture.setText(String.valueOf(car.getYear()));
            mCarEngineCylinders.setText(car.getEngineCylinders());
            mCarVolume.setText(car.getVolume());
            mCarBHP.setText(String.valueOf(car.getHpr()));
            mCarFuel.setText(car.getFuel().toString());
          }
          break;
        case RESULT_CANCELED:
          showWhiteToastMessage(getIntent().getExtras().getString(Constants.CAR_EDIT));
          break;
      }

    }
  }


}
