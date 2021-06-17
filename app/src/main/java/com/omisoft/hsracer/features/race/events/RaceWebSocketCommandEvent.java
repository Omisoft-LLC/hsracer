package com.omisoft.hsracer.features.race.events;

import android.os.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Used to send events to race web socket
 * Created by dido on 15.06.17.
 */
@Getter
@Setter
@AllArgsConstructor
public class RaceWebSocketCommandEvent {

  private final Message message;


}
