package com.omisoft.hsracer.features.login;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.os.SystemClock;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.features.BaseEspressoTest;
import com.omisoft.hsracer.features.home.HomeActivity;
import com.omisoft.hsracer.features.register.RegisterActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test to simulate user login with correct and incorrect credentials
 * Created by developer on 20.07.17.
 */
@RunWith(AndroidJUnit4.class)
public class LoginUserEspressoTest extends BaseEspressoTest {

  @Rule
  public IntentsTestRule<LoginActivity> mActivityRule = new IntentsTestRule<>(LoginActivity.class);

  @Test
  public void incorrectEmail() {
    switchWifiState(getActivityInstance(), true);
    performEditText(R.id.email, clearText(), typeText("nothing"), closeSoftKeyboard());
    performEditText(R.id.password, clearText(), typeText("asd"), closeSoftKeyboard());
    performClickOnView(R.id.login_btn, click());
    systemSleep(1000);
  }

  @Test
  public void incorrectPassword() {
    switchWifiState(getActivityInstance(), true);
    performEditText(R.id.email, clearText(), typeText("dev0@abv.bg"), closeSoftKeyboard());
    performEditText(R.id.password, clearText(), typeText("a complete bullshit"), closeSoftKeyboard());
    performClickOnView(R.id.login_btn, click());
    systemSleep(1000);
  }

  @Test
  public void noEmailCredentials() {
    switchWifiState(getActivityInstance(), true);
    performEditText(R.id.email, clearText());
    performEditText(R.id.password, clearText(), typeText("asd"), closeSoftKeyboard());
    performClickOnView(R.id.login_btn, click());
    SystemClock.sleep(1000);
  }

  @Test
  public void noPasswordCredentials() {
    switchWifiState(getActivityInstance(), true);
    performEditText(R.id.email, clearText(), typeText("dev0@abv.bg"), closeSoftKeyboard());
    performEditText(R.id.password, clearText());
    performClickOnView(R.id.login_btn, click());
    SystemClock.sleep(1000);
  }

  @Test
  public void incorrectEmailAndPassword() {
    switchWifiState(getActivityInstance(), true);
    performEditText(R.id.email, clearText(), typeText("dsaasd@abv.bg"), closeSoftKeyboard());
    performEditText(R.id.password, clearText(), typeText("bullshit"), closeSoftKeyboard());
    onView(withId(R.id.login_btn)).perform(click());
    SystemClock.sleep(1000);
  }

  @Test
  public void noWifiConnection() {
    switchWifiState(getActivityInstance(), false);
    onView(withId(R.id.email)).perform(clearText(), typeText("dev0@abv.bg"), closeSoftKeyboard());
    onView(withId(R.id.password)).perform(clearText(), typeText("asd"), closeSoftKeyboard());
    onView(withId(R.id.login_btn)).perform(click());
    SystemClock.sleep(1000);
  }

  @Test
  public void correctEmailAndPassword() {
    switchWifiState(getActivityInstance(), true);
    onView(withId(R.id.email)).perform(clearText(), typeText("dev0@abv.bg"), closeSoftKeyboard());
    onView(withId(R.id.password)).perform(clearText(), typeText("asd"), closeSoftKeyboard());
    onView(withId(R.id.login_btn)).perform(click());
    systemSleep(1500);

    checkIfActivityIsTheSame(HomeActivity.class);
  }

  @Test
  public void registerButtonClick() {
    performClickOnView(R.id.register_btn, click());
    systemSleep(1500);

    checkIfActivityIsTheSame(RegisterActivity.class);
  }
}
