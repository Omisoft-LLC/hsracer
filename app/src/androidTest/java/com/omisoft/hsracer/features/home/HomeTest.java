package com.omisoft.hsracer.features.home;

import static android.support.test.espresso.action.ViewActions.click;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.features.BaseEspressoTest;
import com.omisoft.hsracer.features.race.CreateRaceActivity;
import com.omisoft.hsracer.features.race.JoinRaceActivity;
import com.omisoft.hsracer.features.results.ResultsActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by developer on 25.07.17.
 */
@RunWith(AndroidJUnit4.class)
public class HomeTest extends BaseEspressoTest{

  @Rule
  public IntentsTestRule<HomeActivity> mActivityRule = new IntentsTestRule<>(HomeActivity.class);

  @Test
  public void goToCreate() {
    performClickOnView(R.id.create_race_home_btn,click());
    systemSleep(3000);
    checkIfActivityIsTheSame(CreateRaceActivity.class);
  }

  @Test
  public void goToJoin() {
    performClickOnView(R.id.join_race_home_btn,click());
    systemSleep(3000);
    checkIfActivityIsTheSame(JoinRaceActivity.class);
  }

  @Test
  public void goToResults() {
    performClickOnView(R.id.results_btn,click());
    systemSleep(3000);
    checkIfActivityIsTheSame(ResultsActivity.class);
  }
}
