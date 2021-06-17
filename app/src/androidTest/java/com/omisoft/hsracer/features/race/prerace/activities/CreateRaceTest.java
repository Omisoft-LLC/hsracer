package com.omisoft.hsracer.features.race.prerace.activities;

import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.features.BaseEspressoTest;
import com.omisoft.hsracer.features.home.HomeActivity;
import com.omisoft.hsracer.model.Car;
import java.util.ArrayList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Create Race Test
 * Created by developer on 20.07.17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateRaceTest extends BaseEspressoTest {

  private static ArrayList<Car> cars = new ArrayList<>();

  @Rule
  public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(
      HomeActivity.class);

  @Test
  public void correctDragRaceWithVideo() {
    switchWifiState(getActivityInstance(), true);
    systemSleep(5000);
    performClickOnView(R.id.create_race_home_btn, click());
    systemSleep(1000);
    performEditText(R.id.race_name, clearText(), typeText("Sprint race testing"),
        closeSoftKeyboard());
    performEditText(R.id.race_description, clearText(), typeText("This is a sprint race"),
        closeSoftKeyboard());
    performClickOnView(R.id.race_type_spinner, scrollTo(), click());
    chooseFromSpinner(String.class, "Drag", click());
    performEditText(R.id.race_distance_sprint_race_frag, scrollTo(), clearText(), typeText("50"),
        closeSoftKeyboard());

  }

  @Test
  public void correctDragRaceNoVideo() {
    switchWifiState(getActivityInstance(), true);
    systemSleep(5000);
    performClickOnView(R.id.create_race_home_btn, click());
    systemSleep(1000);

    performEditText(R.id.race_name, clearText(), typeText("Sprint race testing"),
        closeSoftKeyboard());
    performEditText(R.id.race_description, clearText(), typeText("This is a sprint race"),
        closeSoftKeyboard());
    performClickOnView(R.id.race_type_spinner, scrollTo(), click());
    chooseFromSpinner(String.class, "Drag", click());
    performEditText(R.id.race_distance_sprint_race_frag, scrollTo(), clearText(), typeText("50"),
        closeSoftKeyboard());
    performClickOnView(R.id.video_check_box, scrollTo(), click());


  }

  @Test
  public void correctTopSpeedRaceWithVideo() {
    switchWifiState(getActivityInstance(), true);
    systemSleep(5000);
    performClickOnView(R.id.create_race_home_btn, click());
    systemSleep(1000);

    performEditText(R.id.race_name, clearText(), typeText("Sprint race testing"),
        closeSoftKeyboard());
    performEditText(R.id.race_description, clearText(), typeText("This is a sprint race"),
        closeSoftKeyboard());
    performClickOnView(R.id.race_type_spinner, scrollTo(), click());
    chooseFromSpinner(String.class, "Top Speed", scrollTo(), click());
    performEditText(R.id.end_speed_race_frag, scrollTo(), clearText(), typeText("50"),
        closeSoftKeyboard());


  }

  @Test
  public void correctTopSpeedRaceWithNoVideo() {
    switchWifiState(getActivityInstance(), true);
    systemSleep(5000);
    performClickOnView(R.id.create_race_home_btn, click());
    systemSleep(1000);

    performEditText(R.id.race_name, clearText(), typeText("Sprint race testing"),
        closeSoftKeyboard());
    performEditText(R.id.race_description, clearText(), typeText("This is a sprint race"),
        closeSoftKeyboard());
    performClickOnView(R.id.race_type_spinner, scrollTo(), click());
    chooseFromSpinner(String.class, "Top Speed", scrollTo(), click());
    performEditText(R.id.end_speed_race_frag, scrollTo(), clearText(), typeText("50"),
        closeSoftKeyboard());
    performClickOnView(R.id.video_check_box, scrollTo(), click());


  }

  @Test
  public void missingDragRaceDistance() {
    switchWifiState(getActivityInstance(), true);
    systemSleep(5000);
    performClickOnView(R.id.create_race_home_btn,click());
    systemSleep(1000);

    performEditText(R.id.race_name, clearText(), typeText("Sprint race testing"),
        closeSoftKeyboard());
    performEditText(R.id.race_description, clearText(), typeText("This is a sprint race"),
        closeSoftKeyboard());
    performClickOnView(R.id.race_type_spinner, scrollTo(), click());
    chooseFromSpinner(String.class, "Drag", scrollTo(), click());
    performClickOnView(R.id.create_race_btn,scrollTo(), click());

  }

  @Test
  public void missingTopSpeedRaceSpeed() {
    switchWifiState(getActivityInstance(), true);
    systemSleep(5000);
    performClickOnView(R.id.create_race_home_btn,click());
    systemSleep(1000);
    performEditText(R.id.race_name, clearText(), typeText("Sprint race testing"),
        closeSoftKeyboard());
    performEditText(R.id.race_description, clearText(), typeText("This is a sprint race"),
        closeSoftKeyboard());
    performClickOnView(R.id.race_type_spinner, scrollTo(), click());
    chooseFromSpinner(String.class, "Top Speed", scrollTo(), click());
    performClickOnView(R.id.create_race_btn,scrollTo(), click());

  }

  @Test
  public void incorrectDragRace() {
    switchWifiState(getActivityInstance(), true);
    systemSleep(5000);
    performClickOnView(R.id.create_race_home_btn,click());
    systemSleep(1000);
    performEditText(R.id.race_name, clearText(), typeText("Sprint race testing"),
        closeSoftKeyboard());
    performEditText(R.id.race_description, clearText(), typeText("This is a sprint race"),
        closeSoftKeyboard());
    performClickOnView(R.id.race_type_spinner, scrollTo(), click());
    chooseFromSpinner(String.class, "Drag", click());
    performEditText(R.id.race_distance_sprint_race_frag, scrollTo(), clearText(),
        typeText("5435434634346350"),
        closeSoftKeyboard());
    performClickOnView(R.id.create_race_btn,scrollTo(), click());

  }

  @Test
  public void incorrectTopSpeedRace() {
    switchWifiState(getActivityInstance(), true);
    systemSleep(5000);
    performClickOnView(R.id.create_race_home_btn,click());
    systemSleep(1000);
    performEditText(R.id.race_name, clearText(), typeText("Sprint race testing"),
        closeSoftKeyboard());
    performEditText(R.id.race_description, clearText(), typeText("This is a sprint race"),
        closeSoftKeyboard());
    performClickOnView(R.id.race_type_spinner, scrollTo(), click());
    chooseFromSpinner(String.class, "Top Speed", scrollTo(), click());
    performEditText(R.id.end_speed_race_frag, scrollTo(), clearText(), typeText("345675"),
        closeSoftKeyboard());
    performClickOnView(R.id.create_race_btn,scrollTo(), click());

  }


  @Test
  public void notValidNumberOfRacers() {
    switchWifiState(getActivityInstance(), true);
    systemSleep(5000);
    performClickOnView(R.id.create_race_home_btn,click());
    systemSleep(1000);
    performEditText(R.id.race_name, clearText(), typeText("Sprint race testing"),
        closeSoftKeyboard());
    performEditText(R.id.race_description, clearText(), typeText("This is a sprint race"),
        closeSoftKeyboard());
    performClickOnView(R.id.race_type_spinner, scrollTo(), click());
    chooseFromSpinner(String.class, "Top Speed", scrollTo(), click());
    performEditText(R.id.end_speed_race_frag, scrollTo(), clearText(), typeText("50"),
        closeSoftKeyboard());
    performEditText(R.id.number_of_other_competitors, scrollTo(), clearText(), typeText("9"),
        closeSoftKeyboard());
    performClickOnView(R.id.create_race_btn,scrollTo(), click());

  }

  @Test
  public void noInternetConnection() {
    performClickOnView(R.id.create_race_home_btn,click());
    systemSleep(1000);
    switchWifiState(getActivityInstance(), false);
    performEditText(R.id.race_name, clearText(), typeText("Sprint race testing"),
        closeSoftKeyboard());
    performEditText(R.id.race_description, clearText(), typeText("This is a sprint race"),
        closeSoftKeyboard());
    performClickOnView(R.id.race_type_spinner, scrollTo(), click());
    chooseFromSpinner(String.class, "Top Speed", scrollTo(), click());
    performEditText(R.id.end_speed_race_frag, scrollTo(), clearText(), typeText("50"),
        closeSoftKeyboard());
    performClickOnView(R.id.create_race_btn,scrollTo(), click());
  }
}
