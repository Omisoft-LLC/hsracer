package com.omisoft.hsracer.features.buddyfinder.events;

import android.os.Message;
import lombok.Getter;
import lombok.Setter;

/**
 * Event sent from UI to BF websocket
 * Created by dido on 15.06.17.
 */
@Getter
@Setter
public class BuddyFinderWebSocketCommandEvent {

  private final Message message;

  public BuddyFinderWebSocketCommandEvent(Message message) {
    this.message = message;
  }

}
