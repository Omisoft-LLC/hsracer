package com.omisoft.hsracer.features.profile;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.features.BaseEspressoTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Add Car Test
 * Created by developer on 25.07.17.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class AddCarEspressoTest extends BaseEspressoTest {

  @Rule
  public IntentsTestRule<SaveCarToProfileActivity> mActivityRule =
      new IntentsTestRule<>(SaveCarToProfileActivity.class);

  @Test
  public void addCar() {
    performEditText(R.id.alias_car, typeText("The best car ever"), closeSoftKeyboard());
    performClickOnView(R.id.car_make_spinner, click());
    chooseFromSpinner(String.class, "Audi", scrollTo(), click());
    performClickOnView(R.id.car_model_spinner, click());
    chooseFromSpinner(String.class, "A4", scrollTo(), click());
    performClickOnView(R.id.year_car, click());
    chooseFromSpinner(Integer.class, 2010, scrollTo(), click());
    performEditText(R.id.engine_volume_car, scrollTo(), typeText("2532"));
    performEditText(R.id.bhp_car, scrollTo(), typeText("134"));
    performClickOnView(R.id.fuel_types_spinner, scrollTo(), click());
    chooseFromSpinner(String.class, "Petrol", click());
    performClickOnView(R.id.add_car, scrollTo(), click());
    systemSleep(500);
    checkIfActivityIsTheSame(CarsActivity.class);
  }
}
