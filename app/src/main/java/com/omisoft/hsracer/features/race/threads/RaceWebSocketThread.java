package com.omisoft.hsracer.features.race.threads;

import static com.omisoft.hsracer.constants.URLConstants.RACE_WEB_SOCKET;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.ErrorDTO;
import com.omisoft.hsracer.dto.RaceInfoDTO;
import com.omisoft.hsracer.exceptions.ServerException;
import com.omisoft.hsracer.features.race.events.JoinRaceErrorEvent;
import com.omisoft.hsracer.features.race.events.RaceWebSocketEvent;
import com.omisoft.hsracer.features.race.structures.RaceDataDTO;
import com.omisoft.hsracer.utils.Utils;
import com.omisoft.hsracer.ws.protocol.RaceMessage;
import com.omisoft.hsracer.ws.protocol.dto.JoinRaceDTO;
import com.omisoft.hsracer.ws.protocol.dto.JoinedDTO;
import com.omisoft.hsracer.ws.protocol.dto.NewRaceDTO;
import com.omisoft.hsracer.ws.protocol.dto.RaceCreatedDTO;
import com.omisoft.hsracer.ws.protocol.dto.RaceDTO;
import com.omisoft.hsracer.ws.protocol.dto.RaceStatusDTO;
import com.omisoft.hsracer.ws.protocol.dto.RacerDataDTO;
import com.omisoft.hsracer.ws.protocol.enums.MessageType;
import com.omisoft.hsracer.ws.protocol.enums.RaceStatus;
import com.omisoft.hsracer.ws.protocol.enums.RaceType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;
import lombok.Setter;
import org.greenrobot.eventbus.EventBus;

/**
 * Race Web Socket thread
 * Created by Omisoft LLC. on 5/17/17.
 */

public class RaceWebSocketThread extends LongMonitoredThread implements WebSocketListener {

  public static final int NEW_RACE = 101;
  public static final int JOIN_RACE = 201;
  public static final int READY = 301;
  public static final int START_RACE = 401;
  public static final int RACE_DATA = 501;
  public static final int FINISH_RACE = 601;
  public static final int CANCEL_RACE = 701;
  public static final int CANCEL = 801;
  public static final int KICK_RACER = 901;
  public static final int POISON_PILL = 666;
  private static final String TAG = RaceWebSocketThread.class.getName();
  @Getter
  private final Long profileRestId;
  @Getter
  private final BaseApp context;
  @Getter
  @Setter
  private WebSocket ws;
  @Getter
  private final BlockingQueue<Message> commandQueue;
  @Getter
  private final SharedPreferences sharedPreferences;
  @Getter
  private final Long carId;
  @Getter
  private final ObjectMapper mapper;
  @Getter
  @Setter
  private String name;
  @Getter
  private JoinedDTO joinedDTO;

  public RaceWebSocketThread() {
    this.context = Utils.getBaseApp();
    setName("Race Web Socket thread");
    commandQueue = new LinkedBlockingQueue<>();
    mapper = context.getObjectMapper();
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    this.profileRestId = sharedPreferences.getLong(Constants.REST_ID, 0);
    this.carId = sharedPreferences.getLong(Constants.CAR_REST_ID, 0);
  }

  /**
   * Copy constrictor, needed by Monitor Thread
   */
  public RaceWebSocketThread(RaceWebSocketThread original) {
    this.context = original.getContext();
    this.name = original.getName();
    this.ws = original.getWs();
    this.commandQueue = original.getCommandQueue();
    mapper = this.getMapper();
    sharedPreferences = original.getSharedPreferences();
    this.profileRestId = original.getProfileRestId();
    this.carId = original.getCarId();
    threadState = ThreadState.WAITING;
    this.joinedDTO = original.getJoinedDTO();
  }


  @Override
  public void run() {
    threadState = ThreadState.RUNNING;
    if (ws == null) {
      ws = context.createWebSocket(RACE_WEB_SOCKET);
      ws.addListener(this);
      try {
        ws.connect();
      } catch (WebSocketException e) {
        e.printStackTrace();
        threadState = ThreadState.CRASHED;
      }
    }

    try {
      while (threadState == ThreadState.RUNNING) {
        if (ws != null && ws.getState() == WebSocketState.OPEN) {
          Message message;

          message = commandQueue.take();

          if (message != null) {
            Bundle bundle = message.getData();

            switch (message.what) {
              case NEW_RACE:
                newRace(bundle);
                break;
              case JOIN_RACE:
                joinRace(bundle);
                break;
              case READY:
                readyToRace();
                break;
              case START_RACE:
                startRace();
                break;
              case RACE_DATA:
                sendRaceDataToServer(bundle);
                break;
              case FINISH_RACE:
                finishRace();
                break;
              case CANCEL_RACE:
                cancelRace(bundle);
                break;
              case CANCEL:
                cancel();
                break;
              case KICK_RACER:
                kickRacer(bundle);
                break;
              case POISON_PILL:
                threadState = ThreadState.FINISHED;
                ws.disconnect();
                break;
            }
          }
        }

      }

    } catch (ServerException | InterruptedException e) {
      Log.wtf(TAG, "Race Socket Exception", e);
      // This swallows interrupted exception
      EventBus.getDefault().post(new ServerException(e));
      threadState = ThreadState.CRASHED;

    } finally {
      if (ws != null && threadState == ThreadState.FINISHED && !WebSocketState.CLOSED
          .equals(ws.getState())) {
        ws.sendClose();
      }
    }
  }

  /**
   * That method is called when a player decides to cancel the race in the time of racing
   */
  private void cancel() throws ServerException {
    RaceDTO raceDTO = new RaceDTO();
    raceDTO.setRaceId(sharedPreferences.getLong(Constants.RACE_ID, 0));
    raceDTO.setProfileRestId(profileRestId);
    RaceMessage raceMessage = new RaceMessage();
    raceMessage.setType(MessageType.CANCEL);
    raceMessage.setPayload(raceDTO);
    try {
      ws.sendText(mapper.writeValueAsString(raceMessage));
    } catch (JsonProcessingException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));
      throw new ServerException(e);
    }
  }

  /**
   * This method is called when a player decides to cancel the race right before the race is started
   */
  private void cancelRace(Bundle bundle) throws ServerException {
    RaceDTO raceDTO = new RaceDTO();
    raceDTO.setRaceId(bundle.getLong(Constants.RACE_ID));
    raceDTO.setProfileRestId(profileRestId);
    RaceMessage raceMessage = new RaceMessage();
    raceMessage.setPayload(raceDTO);
    raceMessage.setType(MessageType.CANCEL_RACE);
    Log.wtf(TAG, "CANCEL_RACE send to SERVER");
    try {
      ws.sendText(mapper.writeValueAsString(raceMessage));
    } catch (JsonProcessingException e) {
      throw new ServerException(e);
    }
  }

  /**
   * Called when a new race is created does not matter of what type it is.
   */
  private void newRace(Bundle newRaceBundle) throws ServerException {
    RaceInfoDTO raceInfoDTO = newRaceBundle.getParcelable(RaceInfoDTO.class.getName());
    NewRaceDTO newRaceDTO = new NewRaceDTO();
    RaceType raceType = raceInfoDTO.getRaceType();
    newRaceDTO.setRaceType(raceType);
    newRaceDTO.setCountRacers(raceInfoDTO.getCountRacers());
    newRaceDTO.setCreatorId(raceInfoDTO.getCreatorId());
    newRaceDTO.setDescription(raceInfoDTO.getDescription());
    newRaceDTO.setName(raceInfoDTO.getName());

    switch (raceType) {
      case DRAG:
        newRaceDTO.setDistance(raceInfoDTO.getDistance());
        break;
      case TOP_SPEED:
        newRaceDTO.setStartSpeed(0);
        newRaceDTO.setEndSpeed(raceInfoDTO.getEndSpeed());
        break;
      case CANNONBALL: {
        newRaceDTO.setFinishLat(raceInfoDTO.getFinishLat());
        newRaceDTO.setFinishLng(raceInfoDTO.getFinishLng());
        newRaceDTO.setFinishAddress(raceInfoDTO.getFinishAddress());
      }
      default:
        break;
    }

    RaceMessage raceMessage = new RaceMessage();
    raceMessage.setType(MessageType.NEW_RACE);
    raceMessage.setPayload(newRaceDTO);
    try {
      ws.sendText(mapper.writeValueAsString(raceMessage));
    } catch (JsonProcessingException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));
      throw new ServerException(e);
    }
  }

  /**
   * Called when a new player is joining in the room for racing.
   * @param newRaceBundle
   * @throws ServerException
   */
  private void joinRace(Bundle newRaceBundle) throws ServerException {
    JoinRaceDTO joinRaceDTO = new JoinRaceDTO();
    joinRaceDTO.setRaceId(newRaceBundle.getLong(Constants.RACE_ID));
    joinRaceDTO.setAlias(sharedPreferences.getString(Constants.NICK_NAME, "Unknown"));
    joinRaceDTO.setProfileRestId(profileRestId);
    joinRaceDTO.setCarRestId(carId);
    joinRaceDTO.setCreator(sharedPreferences.getBoolean(Constants.CREATOR, false));
    RaceMessage raceMessage = new RaceMessage();
    raceMessage.setType(MessageType.JOIN_RACE);
    raceMessage.setPayload(joinRaceDTO);
    try {
      ws.sendText(mapper.writeValueAsString(raceMessage));
    } catch (JsonProcessingException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));
      throw new ServerException(e);
    }
  }

  /**
   * Kicks a racer from the room if the Creator of the room swipes out the view.
   * @param bundle
   * @throws ServerException
   */
  private void kickRacer(Bundle bundle) throws ServerException {
    RaceMessage raceMessage = new RaceMessage();
    raceMessage.setType(MessageType.KICK_RACER);
    raceMessage.setPayload(bundle.get("kick_racer"));
    try {
      ws.sendText(mapper.writeValueAsString(raceMessage));
    } catch (JsonProcessingException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));
      throw new ServerException(e);
    }
  }

  /**
   * Called when user clicks on the ready button in {@link com.omisoft.hsracer.features.race.PreRaceSummaryActivity}.
   * @throws ServerException
   */
  private void readyToRace() throws ServerException {
    RaceDTO raceDTO = new RaceDTO();
    raceDTO.setRaceId(sharedPreferences.getLong(Constants.RACE_ID, 0));
    raceDTO.setProfileRestId(profileRestId);
    RaceMessage raceMessage = new RaceMessage();
    raceMessage.setType(MessageType.READY);
    raceMessage.setPayload(raceDTO);
    try {
      Log.wtf(TAG, "SEND MESSAGE READY TO SERVER" + raceDTO.toString());
      ws.sendText(mapper.writeValueAsString(raceMessage));
    } catch (JsonProcessingException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));
      throw new ServerException(e);
    }
  }

  /**
   * This method is called when the creator of the race click the staring race button
   * @throws ServerException
   */
  private void startRace() throws ServerException {
    RaceDTO raceDTO = new RaceDTO();
    raceDTO.setRaceId(sharedPreferences.getLong(Constants.RACE_ID, 0));
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    raceDTO.setProfileRestId(sharedPreferences.getLong(Constants.REST_ID, 0));
    RaceMessage raceMessage = new RaceMessage();
    raceMessage.setType(MessageType.START_RACE);
    raceMessage.setPayload(raceDTO);
    try {
      Log.wtf(TAG, "SEND MESSAGE START RACE TO SERVER " + raceDTO.toString());
      ws.sendText(mapper.writeValueAsString(raceMessage));
    } catch (JsonProcessingException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));
      throw new ServerException(e);
    }
  }

  /**
   * Sends race data to the server including the information for the current racer
   * @param newRaceBundle
   * @throws ServerException
   */
  private void sendRaceDataToServer(Bundle newRaceBundle) throws ServerException {

    RaceDataDTO raceData = newRaceBundle.getParcelable(RaceDataDTO.class.getName());
    RacerDataDTO racerDataDTO = new RacerDataDTO();
    racerDataDTO.setProfileRestId(profileRestId);
    racerDataDTO.setRaceId(sharedPreferences.getLong(Constants.RACE_ID, 0));
    racerDataDTO.setCurrentDistance(raceData.getGpsDistance());
    racerDataDTO.setCurrentSpeed(raceData.getGpsSpeed());
    racerDataDTO.setT(raceData.getRaceCurrentTime());
    racerDataDTO.setCurrentLat(raceData.getLatitude());
    racerDataDTO.setCurrentLat(raceData.getLongitude());
    racerDataDTO.setLive(sharedPreferences.getBoolean(Constants.LIVE_STREAM, false));
    racerDataDTO.setStreamerApiKey(sharedPreferences.getString(Constants.API_KEY, ""));
    RaceMessage raceMessage = new RaceMessage();
    raceMessage.setType(MessageType.RACER_DATA);
    raceMessage.setPayload(racerDataDTO);
    try {
      Log.wtf(TAG, "SENDING RACE DATA TO SERVER " + racerDataDTO.toString());
      ws.sendText(mapper.writeValueAsString(raceMessage));
    } catch (JsonProcessingException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));

      throw new ServerException(e);
    }
  }

  /**
   * This method is called when the condition for ending the particular type of race is true.
   * @throws ServerException
   */
  private void finishRace() throws ServerException {
    RaceDTO raceDTO = new RaceDTO();
    raceDTO.setRaceId(sharedPreferences.getLong(Constants.RACE_ID, 0));
    raceDTO.setProfileRestId(profileRestId);
    RaceMessage raceMessage = new RaceMessage();
    raceMessage.setType(MessageType.FINISH);
    raceMessage.setPayload(raceDTO);
    try {
      Log.i(TAG, "SEND MESSAGE FINISH RACE TO SERVER " + raceDTO.toString());
      ws.sendText(mapper.writeValueAsString(raceMessage));
    } catch (JsonProcessingException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));
      throw new ServerException(e);
    }
  }


            /**            Listeners for the websocket                   **/


  @Override
  public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
    Log.i(TAG, "STATUS: " + websocket.getState());
  }

  @Override
  public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
    Log.i(TAG, "CONNECTED TO RACE SOCKET");

  }

  @Override
  public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
    Log.i(TAG, "CAN'T CONNECT TO SOCKET", cause);
    if (threadState != ThreadState.FINISHED && threadState != ThreadState.WAITING) {
      ws = websocket.recreate().connect();
    }

  }

  @Override
  public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
      WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
    // Reconnect if crashed or running
    if (threadState != ThreadState.FINISHED && threadState != ThreadState.WAITING) {
      ws = websocket.recreate().connect();
    }

  }

  @Override
  public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override
  public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override
  public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override
  public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override
  public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override
  public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    Log.e(TAG, "GOT PING");

  }

  @Override
  public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    Log.e(TAG, "GOT PONG");

  }

  @Override
  public void onTextMessage(WebSocket websocket, String text) throws Exception {
//    Log.wtf(TAG, "MESSAGE FROM SERVER " + text);
    ObjectMapper mapper = context.getObjectMapper();
    RaceMessage msg = mapper.readValue(text, RaceMessage.class);
    try {
      switch (msg.getType()) {
        case CREATED: {
          JsonNode node = mapper.valueToTree(msg.getPayload());
          RaceCreatedDTO raceCreatedDTO = mapper.treeToValue(node, RaceCreatedDTO.class);
          JoinRaceDTO joinRaceDTO = new JoinRaceDTO();
          joinRaceDTO.setRaceId(raceCreatedDTO.getRaceId());
          Long carId = sharedPreferences.getLong(Constants.CAR_REST_ID, 0);
          joinRaceDTO.setCarRestId(carId);
          joinRaceDTO.setProfileRestId(profileRestId);

          joinRaceDTO.setCreator(sharedPreferences.getBoolean(Constants.CREATOR, false));
          joinRaceDTO.setAlias(sharedPreferences.getString(Constants.NICK_NAME, "Unknown"));
          sendJoinRaceMessage(websocket, mapper, joinRaceDTO);
          break;
        }

        case JOINED: {
          JsonNode node = mapper.valueToTree(msg.getPayload());
          JoinedDTO joinedDTO = mapper.treeToValue(node, JoinedDTO.class);
          Editor editor = sharedPreferences.edit();
          editor.putLong(Constants.RACE_ID, joinedDTO.getRaceId());
          editor.apply();
          this.joinedDTO = joinedDTO;
          sendJoinedMessageToUI(joinedDTO);

          break;
        }
        case RACE_STATUS: {
          JsonNode node = mapper.valueToTree(msg.getPayload());
          RaceStatusDTO raceStatusDTO = mapper.treeToValue(node, RaceStatusDTO.class);
          raceStatus(raceStatusDTO, websocket);
          break;
        }
        case CANCEL: {
          Log.wtf(TAG, "MESSAGE FROM SERVER CANCEL");
          break;
        }
        case CANCEL_RACE: {
          threadState = ThreadState.FINISHED;
          Message m = Message.obtain();
          m.what = RaceStatus.CANCELED.getCode();
          EventBus.getDefault().post(new RaceWebSocketEvent(m, this.getClass().getSimpleName()));
          ws.sendClose();
          break;
        }
        case ERROR: {
          JsonNode node = mapper.valueToTree(msg.getPayload());
          ErrorDTO error = mapper.treeToValue(node, ErrorDTO.class);
          EventBus.getDefault().post(new JoinRaceErrorEvent(error.getDetailedMessage()));
          break;
        }
        default: {
          throw new ServerException(new ErrorDTO("Server error", "Server error"));
        }
      }
    } catch (JsonParseException e) {
      Log.wtf(TAG, "JSON EXCEPTION: ", e);
    } catch (Throwable e) {
      Log.wtf(TAG, "THROWABLE EXCEPTION", e);
    }
  }

  @Override
  public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
    Log.e(TAG, "BINARY MSG PROTOCOL NOT IMPLEMENTED");

  }

  @Override
  public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override
  public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override
  public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override
  public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread)
      throws Exception {

  }

  @Override
  public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread)
      throws Exception {

  }

  @Override
  public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread)
      throws Exception {

  }

  @Override
  public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
    Log.wtf(TAG, "Web Socket Error");
  }

  @Override
  public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame)
      throws Exception {

  }

  @Override
  public void onMessageError(WebSocket websocket, WebSocketException cause,
      List<WebSocketFrame> frames) throws Exception {
  }

  @Override
  public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause,
      byte[] compressed) throws Exception {

  }

  @Override
  public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data)
      throws Exception {

  }

  @Override
  public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame)
      throws Exception {

  }

  @Override
  public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {

  }

  @Override
  public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {

  }

  @Override
  public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers)
      throws Exception {

  }

  private void sendJoinRaceMessage(WebSocket websocket, ObjectMapper mapper,
      JoinRaceDTO joinRaceDTO) {
    RaceMessage raceMessage = new RaceMessage();
    raceMessage.setType(MessageType.JOIN_RACE);
    raceMessage.setPayload(joinRaceDTO);

    try {
      websocket.sendText(mapper.writeValueAsString(raceMessage));
    } catch (JsonProcessingException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));
    }
  }

  private void sendJoinedMessageToUI(JoinedDTO joinedDTO) {

    Message message = Message.obtain();
    message.what = MessageType.JOINED.getCode();
    Bundle preRaceSummaryData = new Bundle();
    preRaceSummaryData.putParcelable(JoinedDTO.class.getName(), joinedDTO);
    message.setData(preRaceSummaryData);
    try {
      EventBus.getDefault()
          .postSticky(new RaceWebSocketEvent(message, this.getClass().getSimpleName()));
    } catch (Throwable e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));
    }
  }

  private void raceStatus(RaceStatusDTO raceStatusDTO, WebSocket websocket)
      throws JsonProcessingException {
    context.getRaceDataManager().setLastRaceStatusMessage(raceStatusDTO);
    Message message = Message.obtain();
    message.getData().putParcelable(RaceStatusDTO.class.getName(), raceStatusDTO);
//TODO: When the race starts here are called 3 times RaceStatusDTO. Consider sending information later when the counter hits GO
    if (joinedDTO != null) {
      message.getData().putParcelable(JoinedDTO.class.getName(), joinedDTO);
    }
    switch (raceStatusDTO.getRaceStatus()) {
      case WAITING:

        message.what = RaceStatus.WAITING.getCode();
        EventBus.getDefault()
            .post(new RaceWebSocketEvent(message, this.getClass().getSimpleName()));
        break;

      case READY:
        message.what = RaceStatus.READY.getCode();
        EventBus.getDefault()
            .post(new RaceWebSocketEvent(message, this.getClass().getSimpleName()));
        break;

      case START_RACE:
        message.what = RaceStatus.START_RACE.getCode();
        EventBus.getDefault()
            .post(new RaceWebSocketEvent(message, this.getClass().getSimpleName()));
        break;

      case RUNNING:
        message.what = RaceStatus.RUNNING.getCode();
        EventBus.getDefault()
            .post(new RaceWebSocketEvent(message, this.getClass().getSimpleName()));
        break;

      case FINISHED:
        RaceDTO raceDTO = new RaceDTO();
        raceDTO.setRaceId(sharedPreferences.getLong(Constants.RACE_ID, 0));
        RaceMessage raceMessage = new RaceMessage();
        raceMessage.setType(MessageType.RACE_RESULT);
        raceMessage.setPayload(raceDTO);
        ObjectMapper mapper = context.getObjectMapper();
        try {
          websocket.sendText(mapper.writeValueAsString(raceMessage));
        } catch (JsonProcessingException e) {
          EventBus.getDefault()
              .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));
        }
        break;
      case RACE_OVER:
        threadState = ThreadState.FINISHED;
        message.what = RaceStatus.RACE_OVER.getCode();
        EventBus.getDefault()
            .post(new RaceWebSocketEvent(message, this.getClass().getSimpleName()));
        ws.sendClose();
        break;

      default:
        break;
    }
  }
}