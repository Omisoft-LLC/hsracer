package com.omisoft.hsracer.features.race;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.adapters.DraggableAdapter;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.constants.FirebaseEvent;
import com.omisoft.hsracer.dto.PublishDTO;
import com.omisoft.hsracer.features.home.HomeActivity;
import com.omisoft.hsracer.features.race.actions.SaveRaceDataToDbAction;
import com.omisoft.hsracer.features.race.structures.RaceDataDTO;
import com.omisoft.hsracer.features.share.ShareRaceDataActivity;
import com.omisoft.hsracer.features.share.actions.ShareServerAction;
import com.omisoft.hsracer.features.share.events.FailedToShareResultsEvent;
import com.omisoft.hsracer.features.share.events.ShareEvent;
import com.omisoft.hsracer.ws.protocol.dto.RaceStatusDTO;
import com.omisoft.hsracer.ws.protocol.dto.RacerSummary;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Race Summary Activity
 * Created by Omisoft LLC. on 5/23/17.
 */

public class RaceSummaryActivity extends BaseActivity {

  private RaceStatusDTO raceStatusDTO;
  private PublishDTO publishDTO;
  private String shareURL;
  private long racingRestID;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.share_results_button)
  Button shareButton;
  @BindView(R.id.continue_button)
  Button homeButton;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_race_summary);

    if (getIntent().hasExtra(RaceStatusDTO.class.getName())) {
      raceStatusDTO = getIntent().getExtras().getParcelable(RaceStatusDTO.class.getName());
    }

    setRecyclerView();
    disableHomeButton();
    disableShareButton();
    shareRaceData();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(RaceStatusDTO.class.getName(), raceStatusDTO);
    outState.putParcelable(PublishDTO.class.getName(), publishDTO);
    outState.putString("shareURL", shareURL);
    outState.putLong("racingRestID", racingRestID);
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    raceStatusDTO = savedInstanceState.getParcelable(RaceStatusDTO.class.getName());
    publishDTO = savedInstanceState.getParcelable(PublishDTO.class.getName());
    shareURL = savedInstanceState.getString("shareURL");
    racingRestID = savedInstanceState.getLong("racingRestID");
  }


  @Override
  public void onBackPressed() {
    Intent i = new Intent(this, HomeActivity.class);
    startActivity(i);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void getValuesFromServer(ShareEvent event) {
    racingRestID = event.getPublishResultDTO().getRacingRestId();
    shareURL = event.getPublishResultDTO().getShareUrl();
    enableShareButton();
  }

  // Button to return to home activity
  public void buttonReturnHome(View view) {
    this.onBackPressed();
  }

  public void buttonShare(View view) {
    goToShareRaceData();
  }

  private void setRecyclerView() {
    // Setup D&D feature and RecyclerView
    RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();
    dragMgr.setInitiateOnMove(false);
    dragMgr.setInitiateOnLongPress(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(dragMgr.createWrappedAdapter(new DraggableAdapter(raceStatusDTO)));
    dragMgr.attachRecyclerView(recyclerView);
  }

  // Method to pass values to PublishDTO for ShareRaceDataActivity
  private PublishDTO createPublishDTO() {
    publishDTO = new PublishDTO();
    RacerSummary publishUserSummary = checkRacerSummary(raceStatusDTO);
    publishDTO.setRaceId(getSharedPreferences().getLong(Constants.RACE_ID, 0));
    publishDTO.setAlias(publishUserSummary.getAlias());
    publishDTO.setPlayerStatus(publishUserSummary.getPlayerStatus());
    publishDTO.setCarRestId(getSharedPreferences().getLong(Constants.CAR_REST_ID, 0));
    publishDTO.setCarName(publishUserSummary.getCarName());
    publishDTO.setProfileRestId(getSharedPreferences().getLong(Constants.REST_ID, 0));
    publishDTO.setRaceTime(publishUserSummary.getT());
    publishDTO.setPosition(publishUserSummary.getPosition());
    List<RaceDataDTO> raceDataDTOList = getApp().getRaceDataManager().getCurrentRaceDataList();
    double avgSpeed = 0;
    double maxSpeed = 0;

    for (RaceDataDTO dataDTO : raceDataDTOList) {
      avgSpeed = avgSpeed + dataDTO.getGpsSpeed();
      maxSpeed = Math.max(maxSpeed, dataDTO.getGpsSpeed());
    }

    publishDTO.setAvgSpeed((int) (avgSpeed / raceDataDTOList.size()));
    publishDTO.setMaxSpeed((int) maxSpeed);
    publishDTO.setRaceDataDTOList(new ArrayList<>(raceDataDTOList));
    return publishDTO;
  }

  // Method to pass the correct data from raceStatusDTO to publishDTO
  private RacerSummary checkRacerSummary(RaceStatusDTO raceStatusDTO) {
    long userRestID = getSharedPreferences().getLong(Constants.REST_ID, 0);
    RacerSummary correctUserSummary = null;
    for (RacerSummary elem : raceStatusDTO.getSummaryList()) {
      if (userRestID == elem.getProfileRestId()) {
        correctUserSummary = elem;
        break;
      }
    }
    return correctUserSummary;
  }

  /**
   * Navigates the user to the next activity. There will be displayed detailed information for the
   * rae
   */
  private void goToShareRaceData() {
    Intent i = new Intent(this, ShareRaceDataActivity.class);
    i.putExtra(PublishDTO.class.getName() + "shareURL", shareURL);
    i.putExtra(PublishDTO.class.getName() + "racingRestID", racingRestID);
    i.putExtra(PublishDTO.class.getName() + "publishDTO", publishDTO);
    startActivity(i);
  }

  private void disableShareButton() {
    shareButton.setEnabled(false);
    shareButton.setVisibility(View.INVISIBLE);
  }

  private void enableShareButton() {
    shareButton.setEnabled(true);
    shareButton.setVisibility(View.VISIBLE);
  }

  private void disableHomeButton() {
    homeButton.setEnabled(false);
    homeButton.setVisibility(View.INVISIBLE);
  }

  private void enableHomeButton() {
    homeButton.setEnabled(true);
    homeButton.setVisibility(View.VISIBLE);
  }


  private void shareRaceData() {
    if (getApp().isConnected()) {
      PublishDTO publishDTO = createPublishDTO();
      getExecutor().submit(new ShareServerAction(getApp(), publishDTO));
      if (!BuildConfig.DEBUG) {
        Bundle firebaseParamBundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseEvent.SHARE_RESULTS, firebaseParamBundle);
      }
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
    getExecutor().submit(new SaveRaceDataToDbAction());
    enableHomeButton();
  }

  /**
   * Received by {@link ShareServerAction} if the sharing failed
   * @param event
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void failedToShareResults(FailedToShareResultsEvent event) {
    showWhiteToastMessage(event.getMessage());
  }
}