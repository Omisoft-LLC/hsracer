package com.omisoft.hsracer.features.race.postrace;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.content.ComponentName;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.features.home.HomeActivity;
import com.omisoft.hsracer.features.race.RaceSummaryActivity;
import com.omisoft.hsracer.ws.protocol.dto.RaceStatusDTO;
import com.omisoft.hsracer.ws.protocol.dto.RacerSummary;
import com.omisoft.hsracer.ws.protocol.enums.RaceStatus;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by developer on 25.07.17.
 */
@Deprecated
@RunWith(AndroidJUnit4.class)
public class RaceSummaryTest {

  private static RaceStatusDTO raceStatusDTO;
  @Mock
  private static ArrayList<RacerSummary> racerSummaryArrayList;

  @Rule
  public IntentsTestRule<RaceSummaryActivity> mActivityRule =
      new IntentsTestRule<>(RaceSummaryActivity.class, true, false);

  @BeforeClass
  public static void initRaceStatusDTO() {
    raceStatusDTO = createRaceStatusDTO();
  }

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    Intent i = new Intent();
    i.putExtra(RaceStatusDTO.class.getName(), raceStatusDTO);
    mActivityRule.launchActivity(i);
  }

  @Test
  public void pressContinue() {
    SystemClock.sleep(3000);
    onView(withId(R.id.continue_button)).perform(click());
    SystemClock.sleep(500);
    intended(hasComponent(new ComponentName(getTargetContext(), HomeActivity.class)));
  }

  @Test
  public void pressShare() {
    SystemClock.sleep(3000);
    onView(withId(R.id.share_results_button)).perform(click());
    SystemClock.sleep(500);
    checkIfActivityIsTheSame();
  }

  private static RaceStatusDTO createRaceStatusDTO() {
    RaceStatusDTO raceStatusDTO = new RaceStatusDTO();
    raceStatusDTO.setRaceStatus(RaceStatus.FINISHED);
    raceStatusDTO.setSummaryList(racerSummaryArrayList);
    return raceStatusDTO;
  }

  // Two methods to check if the activity is the same as the rule activity
  private void checkIfActivityIsTheSame() throws IllegalStateException {
    if (checker()) {
      throw new IllegalStateException("INCORRECT ACTIVITY");
    }
  }

  private boolean checker() {
    boolean failed = false;
    try {
      intended(hasComponent(new ComponentName(getTargetContext(), HomeActivity.class)));
      failed = true;
    } catch (Throwable e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_generic, e, this.getClass().getSimpleName()));

    }
    return failed;
  }
}
