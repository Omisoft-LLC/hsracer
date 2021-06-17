package com.omisoft.hsracer.features.buddyfinder.events;

import android.os.Message;
import lombok.Getter;
import lombok.Setter;

/**
 * Event sent from BF websocket to UI
 * Created by dido on 15.06.17.
 */
@Getter
@Setter
public class BuddyFinderWebSocketEvent {

  private final Message message;

  public BuddyFinderWebSocketEvent(Message message) {
    this.message = message;
  }

}
