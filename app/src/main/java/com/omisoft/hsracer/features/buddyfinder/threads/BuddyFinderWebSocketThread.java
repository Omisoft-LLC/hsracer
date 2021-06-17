package com.omisoft.hsracer.features.buddyfinder.threads;


import static com.omisoft.hsracer.constants.Constants.JOINED_FINDER;
import static com.omisoft.hsracer.constants.Constants.RECEIVED_MESSAGE;
import static com.omisoft.hsracer.constants.Constants.RECEIVER_RACERS_PULL;
import static com.omisoft.hsracer.constants.URLConstants.BUDDY_FINDER_RACE_WEB_SOCKET;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import com.omisoft.hsracer.features.buddyfinder.dto.JoinFinderDTO;
import com.omisoft.hsracer.features.buddyfinder.dto.ResponseMessageDTO;
import com.omisoft.hsracer.features.buddyfinder.dto.SendMessageDTO;
import com.omisoft.hsracer.features.buddyfinder.events.BuddyFinderWebSocketEvent;
import com.omisoft.hsracer.features.race.threads.LongMonitoredThread;
import com.omisoft.hsracer.utils.Utils;
import com.omisoft.hsracer.ws.protocol.RaceMessage;
import com.omisoft.hsracer.ws.protocol.enums.MessageType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;
import lombok.Setter;
import org.greenrobot.eventbus.EventBus;

/**
 * WebSocket to run buddy finder
 * Created by Omisoft LLC. on 6/9/17.
 */

public class BuddyFinderWebSocketThread extends LongMonitoredThread implements WebSocketListener {

  public static final int DISCONNECT_SOCKET = 20;
  public static final int PULL_RACERS = 25;
  public static final int JOIN_FINDER = 1;
  public static final int ASK_TO_RACE = 15;
  private static final String TAG = BuddyFinderWebSocketThread.class.getName();
  private final BaseApp context;

  private WebSocket ws;
  @Getter
  private final BlockingQueue<Message> commandQueue;
  private final Long restId;
  @Getter
  @Setter
  private String name;

  public BuddyFinderWebSocketThread() {
    setName("Buddy Finder WebSocket Thread");
    commandQueue = new LinkedBlockingQueue<>();
    context = Utils.getBaseApp();
    restId = context.getSharedPreferences().getLong(Constants.REST_ID, 0);
  }

  @Override
  public void run() {
    threadState = ThreadState.RUNNING;
    if (ws == null) {
      ws = context.createWebSocket(BUDDY_FINDER_RACE_WEB_SOCKET);
      ws.addListener(this);
      try {
        ws.connect();
      } catch (WebSocketException e) {
        e.printStackTrace();
        threadState = ThreadState.CRASHED;

      }
    }
    while (threadState == ThreadState.RUNNING) {
      if (ws != null && ws.getState() == WebSocketState.OPEN) {
        Message message;
        try {
          message = commandQueue.take();

          Bundle bundle;
          if (message != null) {
            bundle = message.getData();

            switch (message.what) {
              case JOIN_FINDER:
                sendMyPositionAndJoin(bundle);
                break;
              case ASK_TO_RACE:
                askToRace(bundle);
                break;
              case DISCONNECT_SOCKET:
                disconnectSocket();
                break;
              case PULL_RACERS:
                pullRacers(bundle);
                break;
            }

          }

        } catch (InterruptedException e) {
          return;
        }
      }
    }
  }


  private void sendMyPositionAndJoin(Bundle bundle) {

    JoinFinderDTO joinFinderDTO = new JoinFinderDTO();
    joinFinderDTO.setLatitude(bundle.getDouble("latitude"));
    joinFinderDTO.setLongitude(bundle.getDouble("longitude"));
    joinFinderDTO.setNickName(bundle.getString("alias"));
    joinFinderDTO.setProfileRestId(restId);

    RaceMessage raceMessage = new RaceMessage();
    raceMessage.setType(MessageType.JOIN_FINDER);
    raceMessage.setPayload(joinFinderDTO);

    sendMessage(raceMessage);
  }

  private void askToRace(Bundle bundle) {

    SendMessageDTO messageDTO = new SendMessageDTO();
    messageDTO.setMessage(bundle.getString("message"));
    messageDTO.setRestIdFrom(bundle.getLong("myId"));
    messageDTO.setRestIdTo(bundle.getLong("otherRacerId"));

    RaceMessage raceMessage = new RaceMessage();
    raceMessage.setType(MessageType.MESSAGE);
    raceMessage.setPayload(messageDTO);

    sendMessage(raceMessage);
  }

  private void disconnectSocket() {
    ws.disconnect();
  }

  private void pullRacers(Bundle bundle) {

    JoinFinderDTO joinFinderDTO = new JoinFinderDTO();
    joinFinderDTO.setLatitude(bundle.getDouble("latitude"));
    joinFinderDTO.setLongitude(bundle.getDouble("longitude"));
    joinFinderDTO.setProfileRestId(restId);

    RaceMessage raceMessage = new RaceMessage();
    raceMessage.setType(MessageType.PULL_RACERS);
    raceMessage.setPayload(joinFinderDTO);

    sendMessage(raceMessage);
  }

  private void sendMessage(RaceMessage raceMessage) {
    try {
      ws.sendText(context.getObjectMapper().writeValueAsString(raceMessage));
    } catch (JsonProcessingException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));

    }
  }

  // Listener implementation
  @Override
  public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {

  }

  @Override
  public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
    Log.e(TAG, "Buddy finder socket");
  }

  @Override
  public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {

  }

  @Override
  public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
      WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {

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

  }

  @Override
  public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

  }

  @Override
  public void onTextMessage(WebSocket websocket, String text) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RaceMessage msg = mapper.readValue(text, RaceMessage.class);
    switch (msg.getType()) {
      case FINDER_RESULT: {
        JsonNode node = mapper.valueToTree(msg.getPayload());
        List<JoinFinderDTO> racers = mapper
            .readValue(mapper.treeAsTokens(node), new TypeReference<List<JoinFinderDTO>>() {
            });
        displayClosestFive(racers, JOINED_FINDER);
        break;
      }
      case MESSAGE: {
        JsonNode node = mapper.valueToTree(msg.getPayload());
        ResponseMessageDTO response = mapper
            .readValue(mapper.treeAsTokens(node), ResponseMessageDTO.class);
        displayReceivedMessageDialog(response);
      }

      case PULL_RESULT: {
        JsonNode node = mapper.valueToTree(msg.getPayload());
        List<JoinFinderDTO> racers = mapper
            .readValue(mapper.treeAsTokens(node), new TypeReference<List<JoinFinderDTO>>() {
            });
        displayClosestFive(racers, RECEIVER_RACERS_PULL);
      }

      default:
        break;
    }
  }

  private void displayClosestFive(List<JoinFinderDTO> racers, int command) {
    if (racers == null) {
      return;
    }
    if (racers.size() > 5) {
      List<JoinFinderDTO> racersList = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        racersList.add(racers.get(i));
      }
      sendMessageToUIToDisplayRacers(racersList, command);
    } else {
      sendMessageToUIToDisplayRacers(racers, command);
    }
  }

  private void displayReceivedMessageDialog(ResponseMessageDTO response) {
    Message message = Message.obtain();
    message.what = RECEIVED_MESSAGE;

    Bundle racerBundle = new Bundle();
    racerBundle.putParcelable("message", response);

    message.setData(racerBundle);
    EventBus.getDefault().post(new BuddyFinderWebSocketEvent(message));
  }

  private void sendMessageToUIToDisplayRacers(List<JoinFinderDTO> racers, int command) {
    Message message = Message.obtain();
    message.what = command;
    Bundle racerBundle = new Bundle();
    racerBundle.putParcelableArrayList("racers", new ArrayList<>(racers));

    message.setData(racerBundle);
    EventBus.getDefault().post(new BuddyFinderWebSocketEvent(message));
  }

  @Override
  public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {

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
    Log.wtf(TAG, "onError");
  }

  @Override
  public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame)
      throws Exception {

  }

  @Override
  public void onMessageError(WebSocket websocket, WebSocketException cause,
      List<WebSocketFrame> frames) throws Exception {
    Log.wtf(TAG, "onMessageError");
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

}