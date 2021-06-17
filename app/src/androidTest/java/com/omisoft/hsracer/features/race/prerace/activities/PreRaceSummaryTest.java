package com.omisoft.hsracer.features.race.prerace.activities;

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
import com.omisoft.hsracer.features.race.PreRaceSummaryActivity;
import com.omisoft.hsracer.features.race.RaceActivity;
import com.omisoft.hsracer.ws.protocol.dto.JoinedDTO;
import com.omisoft.hsracer.ws.protocol.dto.RacerSummary;
import com.omisoft.hsracer.ws.protocol.enums.RaceType;
import java.util.ArrayList;
import org.junit.Before;
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
public class PreRaceSummaryTest {

  @Mock
  private static RacerSummary racerSummary;
  @Mock
  private static ArrayList<RacerSummary> racerSummaryArrayList;

  @Rule
  public IntentsTestRule<PreRaceSummaryActivity> mActivityRule = new IntentsTestRule<>(
      PreRaceSummaryActivity.class);

//  @BeforeClass
//  public static void initJoinedDTO() {
//    joinedDTO = createJoinedDTO();
//  }

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    JoinedDTO joinedDTO = createJoinedDTO();
    Intent i = new Intent();
    i.putExtra(JoinedDTO.class.getName(), joinedDTO);
    mActivityRule.launchActivity(i);
  }

  @Test
  public void goToRace() {
    SystemClock.sleep(5000);
    onView(withId(R.id.ready_to_race_btn)).perform(click());
    SystemClock.sleep(5000);
    intended(hasComponent(new ComponentName(getTargetContext(), RaceActivity.class)));
  }

  private static JoinedDTO createJoinedDTO() {
    JoinedDTO joinedDTO = new JoinedDTO();
    joinedDTO.setCarName("Audi");
    joinedDTO.setCountRacers(1);
    joinedDTO.setDistance(100);
    joinedDTO.setEndSpeed(0);
    joinedDTO.setRaceType(RaceType.DRAG);
    joinedDTO.setRaceId(1L);
    joinedDTO.setStartSpeed(0);
    racerSummaryArrayList.add(racerSummary);
    joinedDTO.setSummaryList(racerSummaryArrayList);
    return joinedDTO;
  }

//  private static RacerSummary createRacerSummary() {
//    RacerSummary racerSummary = new RacerSummary();
//
//    return racerSummary;
//  }
//
//  private static ArrayList<RacerSummary> createRacerSummaryArray(RacerSummary racerSummary) {
//    ArrayList<RacerSummary> racerSummaryArrayList = new ArrayList<>();
//    racerSummaryArrayList.add(racerSummary);
//    return racerSummaryArrayList;
//  }
}
