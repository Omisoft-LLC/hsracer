package com.omisoft.hsracer.features.race.events;

import android.os.Message;
import com.omisoft.hsracer.common.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Event sent from websocket to UI
 * Created by dido on 15.06.17.
 */
@Getter
@Setter
@AllArgsConstructor
public class RaceWebSocketEvent extends BaseEvent {

  private final Message message;

  public RaceWebSocketEvent(Message message, String from, String... to) {
    super(from, to);

    this.message = message;
  }

}
