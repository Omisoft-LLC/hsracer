package com.omisoft.hsracer.features.race;

import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.features.BaseEspressoTest;
import com.omisoft.hsracer.features.login.LoginActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This test will pass only if in the Profile are added cars!
 */
@android.support.test.filters.LargeTest
@RunWith(AndroidJUnit4.class)
public class SingleDragRaceEspressoTest extends BaseEspressoTest {

  private static final String SPACE = " ";

  @Rule
  public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(
      LoginActivity.class);


  @Test
  public void singleDragRaceEspressoTest() throws InterruptedException {
    performEditText(R.id.email, clearText(), typeText("dev1@abv.bg"), closeSoftKeyboard());
    performEditText(R.id.password, clearText(), typeText("asd"), closeSoftKeyboard());
    performClickOnView(R.id.login_btn, click());
    systemSleep(1000);

    performClickOnView(R.id.create_race_home_btn, click());
    systemSleep(1000);

    performEditText(R.id.race_name, clearText(), typeText("Performing some race out of Ruse"),
        closeSoftKeyboard());
    performEditText(R.id.race_description, clearText(),
        typeText("To see if I am going to go ahead from Ivan"),
        closeSoftKeyboard());
    performEditText(R.id.race_distance_sprint_race_frag, scrollTo(), clearText(), typeText("50"),
        closeSoftKeyboard());
    performClickOnView(R.id.create_race_btn, scrollTo(), click());
    systemSleep(2000);

    performClickOnView(R.id.ready_to_race_btn, scrollTo(), click());
    systemSleep(2000);

    performClickOnView(R.id.start_race_button, click());
    systemSleep(23000);

    performClickOnView(R.id.share_results_button, click());
    systemSleep(1000);

    performClickOnView(R.id.share_to_fb, scrollTo(), click());
    performClickOnView(R.id.button_share, click());
    systemSleep(1000);

    performClickOnView(R.id.button_share_video, click());
    systemSleep(20000);

    performClickOnView(R.id.button_upload_to_home, click());

  }
}

