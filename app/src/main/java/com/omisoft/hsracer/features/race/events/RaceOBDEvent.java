package com.omisoft.hsracer.features.race.events;

import android.os.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Event sent from OBD adapter to UI/DB
 * Created by dido on 03.08.17.
 */
@Getter
@Setter
@AllArgsConstructor
public class RaceOBDEvent {

  private final Message message;

}
