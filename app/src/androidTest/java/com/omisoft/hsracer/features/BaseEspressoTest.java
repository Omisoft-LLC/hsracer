package com.omisoft.hsracer.features;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static com.omisoft.hsracer.common.BaseActivity.GPS_PERMISSION_REQUEST;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.test.espresso.ViewAction;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import java.util.Collection;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * Base Test
 * Created by developer on 23.10.17.
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BaseEspressoTest {

  private static final String SPACE = " ";

  @Before
  public void grantPhonePermissions() {
    // In M+, trying to call a number will trigger a runtime dialog. Make sure
    // the permission is granted before running this test.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      getInstrumentation().getUiAutomation().executeShellCommand(
          "pm grant " + getTargetContext().getPackageName()
              + SPACE + Manifest.permission.CAMERA);

      getInstrumentation().getUiAutomation().executeShellCommand(
          "pm grant " + getTargetContext().getPackageName()
              + SPACE + Manifest.permission.WRITE_EXTERNAL_STORAGE);
      getInstrumentation().getUiAutomation().executeShellCommand(
          "pm grant " + getTargetContext().getPackageName()
              + SPACE + Manifest.permission.ACCESS_FINE_LOCATION);
      getInstrumentation().getUiAutomation().executeShellCommand(
          "pm grant " + getTargetContext().getPackageName()
              + SPACE + Manifest.permission.ACCESS_COARSE_LOCATION);
      getInstrumentation().getUiAutomation().executeShellCommand(
          "pm grant " + getTargetContext().getPackageName()
              + Manifest.permission.RECORD_AUDIO);
      getInstrumentation().getUiAutomation().executeShellCommand(
          "pm grant " + getTargetContext().getPackageName()
              + SPACE + GPS_PERMISSION_REQUEST);
    }
  }

  public void performClickOnView(int id, ViewAction... viewAction) {
    onView(withId(id)).perform(viewAction);
  }

  public void systemSleep(long timeToSleep) {
    SystemClock.sleep(timeToSleep);
  }

  public void performEditText(int id, ViewAction... viewAction) {
    onView(withId(id)).perform(viewAction);
  }

  public void performEditText(String text, ViewAction... viewAction) {
    onView((withText(text))).perform(viewAction);
  }

  public void chooseFromSpinner(Class<?> classToClickOn, Object item, ViewAction... viewActions) {
    onData(allOf(is(instanceOf(classToClickOn)), is(item))).perform(viewActions);
  }

  public Activity getActivityInstance() {
    final Activity[] currentActivity = {null};
    getInstrumentation().runOnMainSync(new Runnable() {
      public void run() {
        Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
            .getActivitiesInStage(RESUMED);
        if (resumedActivities.iterator().hasNext()) {
          currentActivity[0] = (Activity) resumedActivities.iterator().next();
        }
      }
    });

    return currentActivity[0];
  }

  /**
   * This works only on activities that are launched from an intent
   */
  public void checkIfActivityIsTheSame(Class<?> expectedActivity) throws IllegalStateException {
    intended(hasComponent(new ComponentName(getTargetContext(), expectedActivity)));

  }

  /**
   * Checks if target activity is launched with an intent
   *
   * @param targetActivityName target activity class name
   */
  public void checkIfActivityIsTheSameAndIntentNotLaunched(String targetActivityName)
      throws IllegalStateException {
    intended(hasComponent(targetActivityName), times(0));

  }

  public void switchWifiState(Activity activityInstance, boolean wifiState) {
    WifiManager wifi = (WifiManager) activityInstance.getSystemService(Context.WIFI_SERVICE);
    wifi.setWifiEnabled(wifiState);
  }

  /**
   * Checks if toast is displayed
   */
  public void checkIfToastIsDisplayed(int toastResId, Activity activity) {
    onView(withText(toastResId)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
        .check(matches(isDisplayed()));

  }
}
