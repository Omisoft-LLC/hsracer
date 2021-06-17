package com.omisoft.hsracer.features.profile;

import static com.omisoft.hsracer.constants.Constants.LOCAL_CAR_ID;
import static com.omisoft.hsracer.constants.Constants.SELECTED_CAR;
import static com.omisoft.hsracer.constants.URLConstants.AUTHORIZATION_HEADER;
import static com.omisoft.hsracer.constants.URLConstants.GET_STAT_POS;
import static com.omisoft.hsracer.constants.URLConstants.REST_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.adapters.CarsAdapter;
import com.omisoft.hsracer.common.BaseToolbarActivity;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.statistics.PositionStatDTO;
import com.omisoft.hsracer.features.profile.actions.DeleteCarDbAction;
import com.omisoft.hsracer.features.profile.actions.DeleteCarServerAction;
import com.omisoft.hsracer.features.profile.actions.ListCarsDbAction;
import com.omisoft.hsracer.features.profile.events.FailedToDeleteCarEvent;
import com.omisoft.hsracer.features.profile.events.ListCarsEvent;
import com.omisoft.hsracer.features.profile.events.SuccessDeleteCarEvent;
import com.omisoft.hsracer.features.profile.interfaces.ClickRecycleItemInterface;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Activity for car actions
 * Created by Omisoft LLC. on 4/24/17.
 */

public class CarsActivity extends BaseToolbarActivity implements ClickRecycleItemInterface {

  @BindView(R.id.empty_text_view)
  TextView mNoCars;
  @BindView(R.id.car_recycler_view)
  RecyclerView mCarsRecyclerView;

  private Long profileRestId;
  private CarsAdapter carsAdapter;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cars);
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    this.profileRestId = sharedPreferences.getLong(Constants.REST_ID, 0);
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onResume() {
    super.onResume();
    initCarsListView();
  }

  /**
   * Navigates the user to {@link SaveCarToProfileActivity} giving him the chance to create new car
   * which he can use later for the races.
   */
  public void addCarClickListener(View view) {
    Intent intent = new Intent(CarsActivity.this, SaveCarToProfileActivity.class);
    intent.putExtra("profileRestId", profileRestId);
    startActivity(intent);
  }

  /**
   * Initialize and sets the adapter to the recyclerView. In the next step is performed a call to
   * the DB in which are take the cars created till now from the user
   */
  private void initCarsListView() {
    mCarsRecyclerView.setHasFixedSize(true);
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
    mCarsRecyclerView.setLayoutManager(mLayoutManager);
    getExecutor().submit(new ListCarsDbAction(getApp()));
  }

  /**
   * list with the cars taken from the database is set to the adapter. The call on that method comes
   * from {@link ListCarsDbAction}
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
  public void listCarsEventHandler(ListCarsEvent event) {
    if (event.getCars().size() > 0) {
      mNoCars.setVisibility(View.GONE);
      carsAdapter = new CarsAdapter(event.getCars(), this);
      mCarsRecyclerView.setAdapter(carsAdapter);
    } else {
      carsAdapter = new CarsAdapter(event.getCars(), this);
      mCarsRecyclerView.setAdapter(carsAdapter);
      mNoCars.setVisibility(View.VISIBLE);
    }
    carsAdapter.notifyDataSetChanged();
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putLong(Constants.REST_ID, profileRestId);
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    profileRestId = savedInstanceState.getLong(Constants.REST_ID, profileRestId);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    this.profileRestId = sharedPreferences.getLong(Constants.REST_ID, 0);
    initCarsListView();
  }

  /**
   * That is a callback from the interface {@link ClickRecycleItemInterface}. That callback is
   * called when a view in the recycler view is clicked.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void clickOnView(final View view, final int position) {
    Request request = new Request.Builder()
        .url(REST_URL + GET_STAT_POS).addHeader(AUTHORIZATION_HEADER, getApp().getAuthId()).build();

    getHttpClient().newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {

      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {

        ResponseBody responseBody = response.body();
        if (!response.isSuccessful()) {
          throw new IOException("Unexpected code");
        }
        final PositionStatDTO dto = getObjectMapper()
            .readValue(responseBody.string(), PositionStatDTO.class);
        CarsActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {

            Intent intent = new Intent(CarsActivity.this, DetailsCarActivity.class);
            intent.putExtra(SELECTED_CAR, carsAdapter.getMListWithCars().get(position));
            intent.putExtra(LOCAL_CAR_ID, carsAdapter.getMListWithCars().get(position).getId());
            intent.putExtra(PositionStatDTO.class.getName(), dto);
            if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP) {
              Pair<View, String> p1 = Pair
                  .create(view.findViewById(R.id.car_name_in_cars),
                      getString(R.string.transition_car_name));
              Pair<View, String> p2 = Pair
                  .create(view.findViewById(R.id.make_name_and_model_in_cars),
                      getString(R.string.transition_make_and_model));
              ActivityOptionsCompat options = ActivityOptionsCompat.
                  makeSceneTransitionAnimation(CarsActivity.this, p1, p2);
              startActivity(intent, options.toBundle());
            } else {
              startActivity(intent);
            }
          }


        });
      }
    });
  }

  /**
   * That is a callback from the interface {@link ClickRecycleItemInterface}. When the bucket in one
   * of the views is clicked a car is deleted from the base and server
   */
  @Override
  public void deleteVehicle(int position) {
    if (getApp().isConnected()) {
      getExecutor().submit(new DeleteCarServerAction(getApp(),
          carsAdapter.getMListWithCars().get(position).getRestId(), position));
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }

  /**
   * This method is called after car is deleted successfully from the server . Called from {@link
   * DeleteCarServerAction}. Here is called the executor which deletes the car from DB;
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void deleteCarHandler(SuccessDeleteCarEvent event) {
    getExecutor().submit(new DeleteCarDbAction(getApp(),
        carsAdapter.getMListWithCars().get(event.getPositionInAdapter()).getId()));
    carsAdapter.getMListWithCars().remove(event.getPositionInAdapter());
    carsAdapter.notifyDataSetChanged();
    if (carsAdapter.getMListWithCars().size() == 0) {
      mNoCars.setVisibility(View.VISIBLE);
    }

  }

  /**
   * Called when the server fails to delete a car. Called from {@link DeleteCarServerAction}
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void failedToDeleteCar(FailedToDeleteCarEvent event) {
    showWhiteToastMessage(event.getMessage());
  }
}
