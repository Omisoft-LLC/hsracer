package com.omisoft.hsracer.features.login;

import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.features.BaseEspressoTest;
import com.omisoft.hsracer.features.SplashActivity;
import com.omisoft.hsracer.features.home.HomeActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Login test case
 * Created by dido on 20.10.17.
 */
@android.support.test.filters.LargeTest
@RunWith(AndroidJUnit4.class)
public class FullLoginEspressoTest extends BaseEspressoTest {

  public static final String SPACE = " ";
  @Rule
  public final ActivityTestRule<SplashActivity> main = new ActivityTestRule<>(SplashActivity.class);

  @Test
  public void loginEspressoTest() {
    performClickOnView(R.id.agree_disclaimer_button, click());
    performEditText(R.id.email,clearText(),typeText("dev0@abv.bg"),closeSoftKeyboard());
    performEditText(R.id.password,clearText(),typeText("asd"),closeSoftKeyboard());
    systemSleep(1000);
    checkIfActivityIsTheSame(HomeActivity.class);
  }

}