package com.omisoft.hsracer.features.race.duringrace;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.omisoft.hsracer.features.race.RaceActivity;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * Created by developer on 21.07.17.
 */
@Deprecated
@RunWith(AndroidJUnit4.class)
public class RaceTest {

  @Rule
  public ActivityTestRule<RaceActivity> mActivityRule = new ActivityTestRule<>(RaceActivity.class);

//  @Test
//  public void race() {
//    SystemClock.sleep(2000);
//    onView(withId(R.id.start_stop_layout)).perform(click());
//    SystemClock.sleep(12000);
//    onView(withId(R.id.start_stop_layout)).perform(click());
//    SystemClock.sleep(500);
//  }
}
