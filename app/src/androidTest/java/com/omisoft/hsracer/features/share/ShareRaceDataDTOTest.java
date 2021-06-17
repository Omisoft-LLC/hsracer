package com.omisoft.hsracer.features.share;

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
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.dto.PublishDTO;
import com.omisoft.hsracer.dto.PublishResultDTO;
import com.omisoft.hsracer.features.home.HomeActivity;
import com.omisoft.hsracer.features.share.events.ShareEvent;
import com.omisoft.hsracer.ws.protocol.enums.PlayerStatus;
import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by developer on 24.07.17.
 */
@Deprecated
@RunWith(AndroidJUnit4.class)
public class ShareRaceDataDTOTest {

  private static PublishDTO publishDTO;
  @Mock
  private PublishResultDTO publishResultDTO;

  @Rule
  public IntentsTestRule<ShareRaceDataActivity> mActivityRule =
      new IntentsTestRule<>(ShareRaceDataActivity.class, true, false);

  @BeforeClass
  public static void initPublishDTO() {
    publishDTO = createPublishDTO();
  }

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    Intent i = new Intent();
    i.putExtra(PublishDTO.class.getName(), publishDTO);
    mActivityRule.launchActivity(i);
  }

  @Test
  public void dontShare() {
    onView(withId(R.id.button_share)).perform(click());
    SystemClock.sleep(1000);
    checkIfActivityIsTheSame();
  }

  @Test
  public void shareToHSRacerButNotToFacebook() {
    onView(ViewMatchers.withId(R.id.share_to_fb)).perform(click());
    onView(withId(R.id.button_share)).perform(click());
    EventBus.getDefault().post(new ShareEvent(publishResultDTO, this.getClass().getSimpleName()));
    SystemClock.sleep(1000);
    intended(hasComponent(new ComponentName(getTargetContext(), UploadVideoActivity.class)));
  }

  @Test
  public void shareToHSRacerAndFacebook() {
    onView(withId(R.id.button_share)).perform(click());
    EventBus.getDefault().post(new ShareEvent(publishResultDTO, this.getClass().getSimpleName()));
    SystemClock.sleep(1000);
  }

  private static PublishDTO createPublishDTO() {
    PublishDTO publishDTO = new PublishDTO();
    publishDTO.setPosition(1);
    publishDTO.setCarName("Audi");
    publishDTO.setCarRestId(1L);
    publishDTO.setPlayerStatus(PlayerStatus.FINISHED);
    publishDTO.setAlias("MAH CAR");
    publishDTO.setAvgSpeed(150);
    publishDTO.setMaxSpeed(300);
    publishDTO.setProfileRestId(1L);
    publishDTO.setRaceId(1L);
    return publishDTO;
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
      Log.wtf("ShareRaceDataDTOTest", "checker: ", e);
    }
    return failed;
  }
}
