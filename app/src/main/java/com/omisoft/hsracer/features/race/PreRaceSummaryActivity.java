package com.omisoft.hsracer.features.race;

import static com.omisoft.hsracer.constants.Constants.CONSTANT_LATITUDE;
import static com.omisoft.hsracer.constants.Constants.CONSTANT_LONGITUDE;
import static com.omisoft.hsracer.constants.Constants.DISPLAYED_LOCATION;
import static com.omisoft.hsracer.constants.Constants.NEARBY_DATA_SEPARATOR;
import static com.omisoft.hsracer.constants.Constants.RACE_NAME;
import static com.omisoft.hsracer.features.race.threads.RaceWebSocketThread.CANCEL_RACE;
import static com.omisoft.hsracer.features.race.threads.RaceWebSocketThread.KICK_RACER;
import static com.omisoft.hsracer.features.race.threads.RaceWebSocketThread.READY;
import static com.omisoft.hsracer.ws.protocol.enums.PlayerStatus.KICKED;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import butterknife.BindView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.MessageListener;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.adapters.PreRaceSummaryAdapter;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.BaseToolbarActivity;
import com.omisoft.hsracer.common.events.ServiceCommandEvent;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.features.race.async.URLShorthenerTask;
import com.omisoft.hsracer.features.race.events.RaceWebSocketEvent;
import com.omisoft.hsracer.features.race.fragments.preracesummary.CannonBallPreRaceSummaryFragment;
import com.omisoft.hsracer.features.race.fragments.preracesummary.DragPreRaceSummaryFragment;
import com.omisoft.hsracer.features.race.fragments.preracesummary.TopSpeedPreRaceSummaryFragment;
import com.omisoft.hsracer.holders.PreRaceSummaryHolder;
import com.omisoft.hsracer.services.RaceService;
import com.omisoft.hsracer.ws.protocol.dto.JoinedDTO;
import com.omisoft.hsracer.ws.protocol.dto.RaceStatusDTO;
import com.omisoft.hsracer.ws.protocol.dto.RacerSummary;
import com.omisoft.hsracer.ws.protocol.enums.MessageType;
import com.omisoft.hsracer.ws.protocol.enums.PlayerStatus;
import com.omisoft.hsracer.ws.protocol.enums.RaceStatus;
import com.omisoft.hsracer.ws.protocol.enums.RaceType;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * PreRace Activity
 * Created by Omisoft LLC. on 5/16/17.
 */

public class PreRaceSummaryActivity extends BaseToolbarActivity implements ConnectionCallbacks,
    OnConnectionFailedListener {

  private static final int LEAVE_RACE_CREATOR = 1;
  private static final int LEAVE_RACE_RACER = 2;
  private static final String SHARE_URL = "SHARE_URL";

  @BindView(R.id.race_id_summary)
  TextView mRaceIDTextView;
  @BindView(R.id.total_racers_summary)
  TextView mTotalRacersTextView;
  @BindView(R.id.my_car_summary)
  TextView mCarTextView;
  @BindView(R.id.race_type_summary)
  TextView mRaceTypeTextView;
  @BindView(R.id.race_creator_nickname)
  TextView mRaceCreatorNickname;
  @BindView(R.id.recycler_view_in_pre_race_summary)
  RecyclerView mPlayerStatusRecyclerView;
  private String mRaceName;
  private AlertDialog mAlertDialog = null;
  private JoinedDTO mJoinedDTO;
  private PreRaceSummaryAdapter mPreRaceSummaryAdapter;
  private GoogleApiClient mGoogleApiClient;
  private MessageListener mMessageListener;

  private boolean mMetrical;
  private String shareURL;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pre_race_summary);
    if (savedInstanceState != null) {
      mJoinedDTO = savedInstanceState.getParcelable(JoinedDTO.class.getName());
    } else {
      NotificationManager notificationManager = (NotificationManager) getApplicationContext()
          .getSystemService(
              Context.NOTIFICATION_SERVICE);
      notificationManager.cancel(BaseApp.NOTIFICATION_ID);
      mJoinedDTO = getIntent().getParcelableExtra(JoinedDTO.class.getName());
      mRaceName = getIntent().getStringExtra(RACE_NAME);
    }
    checkWhatSystemIsUsed();
    fillPreRaceSummary(mJoinedDTO);
    // enable Google nearby only if creator
    enableGoogleNearby();
    // Generate shorther share url
    try {
      new URLShorthenerTask(new WeakReference<>(this))
          .execute(new URL(BuildConfig.WEB_URL + "/public/live/" + mJoinedDTO.getRaceId()));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

  }

  /**
   * When the back button is pressed a dialog screen appears. Depends on the person which clicked
   * the back button (creator or not).  A message is shown. If the user is creator and he accept the
   * asking he will be navigated to {@link CreateRaceActivity} and the other participants will be
   * navigated to {@link JoinRaceActivity}
   */
  @Override
  public void onBackPressed() {
    View linearLayout = this.getLayoutInflater()
        .inflate(R.layout.fragment_navigate_back_warning, null);
    SharedPreferences sharedPreferences = getSharedPreferences();
    if (sharedPreferences.getBoolean(Constants.CREATOR, false)) {
      ((TextView) linearLayout.findViewById(R.id.message1))
          .setText(R.string.are_you_sure_leave_race_creator);
      linearLayout.findViewById(R.id.message2).setVisibility(View.GONE);
      linearLayout.findViewById(R.id.message3).setVisibility(View.GONE);
      showDialogOnBackPressed(linearLayout, LEAVE_RACE_CREATOR);
    } else {
      ((TextView) linearLayout.findViewById(R.id.message1))
          .setText(R.string.are_you_sure_leave_race_racer);
      showDialogOnBackPressed(linearLayout, LEAVE_RACE_RACER);
      linearLayout.findViewById(R.id.message2).setVisibility(View.GONE);
      linearLayout.findViewById(R.id.message3).setVisibility(View.GONE);
    }
  }

  @Override
  public void onStop() {
    if (mAlertDialog != null) {
      mAlertDialog.dismiss();
    }
    if (getSharedPreferences().getBoolean(Constants.CREATOR, false)
        && mJoinedDTO.getCountRacers() > 1) {
      unsubscribe();
      if (mGoogleApiClient.isConnected()) {
        mGoogleApiClient.disconnect();
      }
    }
    super.onStop();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;

      case R.id.menu_race_share:
        shareLiveRaceBtnOnClickListener(null);
        return true;
      default:
        return (super.onOptionsItemSelected(item));
    }
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {

    savedInstanceState.putParcelable(JoinedDTO.class.getName(), mJoinedDTO);
    savedInstanceState.putString(SHARE_URL, shareURL);
//    savedInstanceState.putParcelableArrayList(RacerSummary.class.getName(), listWithRacers);
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    mJoinedDTO = savedInstanceState.getParcelable(JoinedDTO.class.getName());
    shareURL = savedInstanceState.getString(SHARE_URL);
    fillPreRaceSummary(mJoinedDTO);
  }


  @Override
  protected void onResume() {
    super.onResume();

    if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
      Log.e(TAG, "THE CLIENT IS CONNECTED");
      mGoogleApiClient.connect();
    }
  }

  /**
   * When the user decide he can click the ready button. He must grant permission to the GPS if its
   * turned off.
   */
  public void readyToRaceBtnOnClickListener(View view) {
    if (getApp().isConnected()) {
      if (!getApp().checkIsGPSEnabled()) {
        showAlertGPS();
        return;
      } else {
        startGPS();
      }

      sendCommandMessageToRaceWebSocketThread(null, READY);

    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }

  /**
   * Share url to race
   */
  public void shareLiveRaceBtnOnClickListener(View view) {

    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.watch_race, shareURL));
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.watch_race_live));
    startActivity(
        Intent.createChooser(shareIntent, getResources().getText(R.string.share_live_race_page)));

  }

  /**
   * Starts GPS
   */
  private void startGPS() {
    Message m = Message.obtain();
    m.what = RaceService.START_GPS_ACTION;
    EventBus.getDefault().post(new ServiceCommandEvent(m));
  }

  /**
   * Checks what system the user choose from Settings
   */
  private void checkWhatSystemIsUsed() {
    SharedPreferences metricPreferences = getSharedPreferences();
    mMetrical = metricPreferences
        .getString(getString(R.string.pref_metric_key), getString(R.string.metric_system))
        .equals(getString(R.string.metric_system));
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onRaceWebSocketEvent(RaceWebSocketEvent event) {
    Message inputMessage = event.getMessage();
    if (inputMessage != null) {
      switch (RaceStatus.fromCode(inputMessage.what)) {
        case READY: {
          startRaceProceduresActivity(inputMessage);
          break;

        }
        case CANCELED: {
          showWhiteToastMessage(R.string.race_canceled);
          finish();
          break;
        }
        case WAITING: {
          RaceStatusDTO raceStatusDTO =
              inputMessage.getData().getParcelable(RaceStatusDTO.class.getName());
          if (raceStatusDTO != null) {

            mJoinedDTO
                .setSummaryList((ArrayList) removeKickedRacer(raceStatusDTO.getSummaryList()));
            if (getSharedPreferences().getBoolean(Constants.CREATOR, false)) {
              mPreRaceSummaryAdapter
                  .setMListWithRacerStatus(removeCreatorFromList(mJoinedDTO.getSummaryList()));
            } else {
              mPreRaceSummaryAdapter.setMListWithRacerStatus(mJoinedDTO.getSummaryList());
            }
            mPreRaceSummaryAdapter.notifyDataSetChanged();
          }
        }
      }
    }
  }

  /**
   * Kicks a racer from the room (if such exists) and navigates him to JoinRaceActivity
   * For the other racers it removes the racer from the list
   *
   * @param summaryList incoming list which needs to be searched if a racer with status KICKED
   * exists
   * @return returns list without players with status KICKED
   */
  private List<RacerSummary> removeKickedRacer(
      List<RacerSummary> summaryList) {

    for (RacerSummary racer : summaryList) {
      if (racer.getPlayerStatus() == PlayerStatus.KICKED) {
        if (racer.getProfileRestId() == getSharedPreferences().getLong(Constants.REST_ID, 0)) {
          showWhiteToastMessage(getString(R.string.kicked_from_race));
          sendCommandMessageToWebSocketThreadToStopThread();
          finish();
        } else {
          summaryList.remove(racer);
        }
      }
    }
    return summaryList;
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onJoinedToTheRace(RaceWebSocketEvent event) {
    Message inputMessage = event.getMessage();
    switch (MessageType.fromCode(inputMessage.what)) {
      case JOINED:
        JoinedDTO joinedDTO = inputMessage.getData().getParcelable(JoinedDTO.class.getName());
        if (joinedDTO != null) {
          mJoinedDTO.setSummaryList(joinedDTO.getSummaryList());
          if (getSharedPreferences().getBoolean(Constants.CREATOR, false)) {
            mPreRaceSummaryAdapter
                .setMListWithRacerStatus(removeCreatorFromList(mJoinedDTO.getSummaryList()));
          } else {
            mPreRaceSummaryAdapter.setMListWithRacerStatus(mJoinedDTO.getSummaryList());
          }
          mPreRaceSummaryAdapter.notifyDataSetChanged();
        }
    }
  }

  /**
   * Navigates to PreRaceSummaryActivity where the the racers need to wait each other till all
   * racers are ready
   */
  private void startRaceProceduresActivity(Message inputMessage) {
    Intent intent = new Intent(PreRaceSummaryActivity.this, RaceActivity.class);
    JoinedDTO joinedDTO = inputMessage.getData().getParcelable(JoinedDTO.class.getName());
    intent.putExtra(JoinedDTO.class.getName(), joinedDTO);

    startActivity(intent);

  }

  /**
   * populate the TextViews in the activity
   */
  private void fillPreRaceSummary(JoinedDTO joinedDTO) {
    RaceType mTypeOfRace = joinedDTO.getRaceType();
    mRaceIDTextView.setText(String.valueOf(joinedDTO.getRaceId()));
    mTotalRacersTextView.setText(String.valueOf(joinedDTO.getCountRacers()));
    mCarTextView.setText(joinedDTO.getCarName());
    mRaceTypeTextView.setText(joinedDTO.getRaceType().name());
    for (RacerSummary r : joinedDTO.getSummaryList()) {
      if (r.isCreator()) {
        mRaceCreatorNickname.setText(r.getRacerNickname());
        break;
      }
    }
    setSummaryFrame(mTypeOfRace, joinedDTO);
    populateRecyclerView(joinedDTO.getSummaryList());
  }

  /**
   * Creates adapter for the recyclerView and populate the recycler view with racers and there
   * information. A separate if is created for the CREATOR where he does not include himself.
   */
  private void populateRecyclerView(List<RacerSummary> summaryList) {
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    mPlayerStatusRecyclerView.setHasFixedSize(true);
    mPlayerStatusRecyclerView.setLayoutManager(mLayoutManager);
    mPreRaceSummaryAdapter = new PreRaceSummaryAdapter(summaryList);
    mPlayerStatusRecyclerView.setAdapter(mPreRaceSummaryAdapter);

    if (getSharedPreferences().getBoolean(Constants.CREATOR, false)) {
      summaryList = removeCreatorFromList(summaryList);
      ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback());
      itemTouchHelper.attachToRecyclerView(mPlayerStatusRecyclerView);
    }
    mPreRaceSummaryAdapter = new PreRaceSummaryAdapter(summaryList);
    mPlayerStatusRecyclerView.setAdapter(mPreRaceSummaryAdapter);
  }

  private List<RacerSummary> removeCreatorFromList(
      List<RacerSummary> summaryList) {
    List<RacerSummary> racersWithoutCreator = new ArrayList<>();
    for (RacerSummary racer : summaryList) {
      if (!racer.isCreator()) {
        racersWithoutCreator.add(racer);
      }
    }
    return racersWithoutCreator;
  }


  /**
   * Callback fot every View in the recycler view. From here the CREATOR can remove racers from the
   * race. The removing is made with cool animation for swapping :D.
   */
  private SimpleCallback callback() {

    return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
      @Override
      public void onSelectedChanged(ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
          final View foregroundView = ((PreRaceSummaryHolder) viewHolder).getMFrontPanel();

          getDefaultUIUtil().onSelected(foregroundView);
        }
      }

      @Override
      public void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX,
          float dY, int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((PreRaceSummaryHolder) viewHolder).getMFrontPanel();
        drawBackground(viewHolder, dX, actionState);
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive);
      }

      @Override
      public void onChildDrawOver(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder,
          float dX,
          float dY, int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((PreRaceSummaryHolder) viewHolder).getMFrontPanel();

        drawBackground(viewHolder, dX, actionState);

        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive);
      }

      @Override
      public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
        final View backgroundView = ((PreRaceSummaryHolder) viewHolder).getMBackgroundPanel();
        final View foregroundView = ((PreRaceSummaryHolder) viewHolder).getMFrontPanel();
        backgroundView.setRight(0);

        getDefaultUIUtil().clearView(foregroundView);
      }

      @Override
      public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
          RecyclerView.ViewHolder target) {
        return true;
      }

      @Override
      public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Bundle bundle = new Bundle();
        RaceStatusDTO raceStatusDTO = new RaceStatusDTO();
        mJoinedDTO.getSummaryList().get(viewHolder.getAdapterPosition() + 1)
            .setPlayerStatus(KICKED);
        raceStatusDTO.setRaceId(mJoinedDTO.getRaceId());
        raceStatusDTO.setSummaryList(mJoinedDTO.getSummaryList());
        raceStatusDTO.setRaceStatus(RaceStatus.WAITING);
        bundle.putParcelable("kick_racer", raceStatusDTO);
        sendCommandMessageToRaceWebSocketThread(bundle, KICK_RACER);
      }
    };
  }

  /**
   * Draw a red background with the text REMOVE so you can remove the racer from the room
   */
  private static void drawBackground(RecyclerView.ViewHolder viewHolder, float dX,
      int actionState) {
    final View backgroundView = ((PreRaceSummaryHolder) viewHolder).getMBackgroundPanel();

    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
      //noinspection NumericCastThatLosesPrecision
      //TODO: Place here an animation for making the front panel transparent with the length of swiping
      backgroundView.setRight((int) Math.max(dX, 0));
    }
  }

  /**
   * Sets a different fragment. It depends on the race type
   */
  private void setSummaryFrame(RaceType raceType, JoinedDTO joinedDTO) {
    switch (raceType) {
      case DRAG:
        DragPreRaceSummaryFragment sprintFragment = new DragPreRaceSummaryFragment();
        sprintFragment.setDistance(distanceConverter(mMetrical, joinedDTO.getDistance()));
        replaceFragment(R.id.race_type_pre_race_summary_frame, sprintFragment);
        break;
      case TOP_SPEED:
        Bundle bundle = new Bundle();
        bundle.putParcelable(JoinedDTO.class.getName(), joinedDTO);
        bundle.putBoolean("mMetrical", mMetrical);
        TopSpeedPreRaceSummaryFragment rollRaceFragment = new TopSpeedPreRaceSummaryFragment();
        rollRaceFragment.setArguments(bundle);

        replaceFragment(R.id.race_type_pre_race_summary_frame, rollRaceFragment);
        break;
      case CANNONBALL:
        bundle = new Bundle();
        Log.wtf(TAG, "setSummaryFrame: " + joinedDTO.getFinishAddress());
        bundle.putString(DISPLAYED_LOCATION, joinedDTO.getFinishAddress());
        bundle.putDouble(CONSTANT_LATITUDE, joinedDTO.getFinishLat());
        bundle.putDouble(CONSTANT_LONGITUDE, joinedDTO.getFinishLng());
        CannonBallPreRaceSummaryFragment cannonBallPreRaceSummaryFragment = new CannonBallPreRaceSummaryFragment();
        cannonBallPreRaceSummaryFragment.setArguments(bundle);

        replaceFragment(R.id.race_type_pre_race_summary_frame, cannonBallPreRaceSummaryFragment);
        break;
    }
  }


  /**
   * Show a dialog for the racer who pressed back. The creator removes all racers from the race and
   * the room on the server is removed. The normal racer remove himself from the room and closes his
   * websocket connection
   */
  private void showDialogOnBackPressed(View linearLayout, final int event) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.are_you_sure_race)
        .setView(linearLayout)
        .setCancelable(true)
        .setPositiveButton(R.string.okay, new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            switch (event) {
              case LEAVE_RACE_CREATOR: {
                Bundle raceBundle = new Bundle();
                raceBundle.putLong(Constants.RACE_ID, mJoinedDTO.getRaceId());
                sendCommandMessageToRaceWebSocketThread(raceBundle, CANCEL_RACE);
                break;
              }
              case LEAVE_RACE_RACER: {
                Bundle raceBundle = new Bundle();
                raceBundle.putLong(Constants.RACE_ID, mJoinedDTO.getRaceId());
                sendCommandMessageToRaceWebSocketThread(raceBundle, CANCEL_RACE);
                sendCommandMessageToWebSocketThreadToStopThread();
                finish();
                break;
              }
            }
          }
        }).setNeutralButton(R.string.cancel, new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {

      }
    });
    mAlertDialog = builder.show();
  }

  private void enableGoogleNearby() {
    if (getSharedPreferences().getBoolean(Constants.CREATOR, false)
        && mJoinedDTO.getCountRacers() > 1) {

      mGoogleApiClient = new Builder(this)
          .addApi(Nearby.MESSAGES_API)
          .addConnectionCallbacks(this)
          .enableAutoManage(this, this)
          .build();
      mMessageListener = new MessageListener() {
        @Override
        public void onFound(com.google.android.gms.nearby.messages.Message message) {
          String messageAsString = new String(message.getContent());
          Log.wtf("NEARBY", "Found message: " + messageAsString);
          showWhiteToastMessage("YEAH WE ARE HERE FINALLY ");
          if (messageAsString.startsWith(BuildConfig.APPLICATION_ID)) {

            String payload = messageAsString
                .substring(BuildConfig.APPLICATION_ID.length() + NEARBY_DATA_SEPARATOR
                    .length());
            showWhiteToastMessage(payload);
          }
        }

        @Override
        public void onLost(com.google.android.gms.nearby.messages.Message message) {
          String messageAsString = new String(message.getContent());
          Log.wtf("NEARBY", "Lost sight of message: " + messageAsString);
        }
      };
    }
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    subscribe();
    publish(BuildConfig.APPLICATION_ID + NEARBY_DATA_SEPARATOR + mJoinedDTO.getRaceId()
        + NEARBY_DATA_SEPARATOR + mRaceCreatorNickname.getText().toString()
        + NEARBY_DATA_SEPARATOR + mRaceName);
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  private void publish(String message) {
    try {
      com.google.android.gms.nearby.messages.Message mActiveMessage = new com.google.android.gms.nearby.messages.Message(
          message.getBytes());
      Nearby.Messages.publish(mGoogleApiClient, mActiveMessage);
    } catch (Exception e) {
      Log.wtf(TAG, "publish: ", e);
    }
  }

  private void subscribe() {
    Log.wtf("NEARBY", "FINALLY SUBSCRIBED");
    Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener);
  }

  private void unsubscribe() {
    if (mGoogleApiClient != null && mMessageListener != null) {
      Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_toolbar_share_sensors, menu);
    AnimationUtils.loadAnimation(getApp(), R.anim.blinking_icons_toolbar);
    MenuItem obd = menu.getItem(1);
    obd.setActionView(R.layout.action_view_for_menu_obd);
    MenuItem gps = menu.getItem(2);
    gps.setActionView(R.layout.action_view_for_menu_gps);
    MenuItem net = menu.getItem(3);
    net.setActionView(R.layout.action_view_for_menu_net);

    return true;
  }


  public void setShareURL(String url) {
    shareURL = url;

  }

}