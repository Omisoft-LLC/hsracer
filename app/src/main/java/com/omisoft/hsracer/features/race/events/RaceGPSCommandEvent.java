package com.omisoft.hsracer.features.race.events;

import android.os.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Event sent from UI to race GPS thread
 * Created by dido on 16.06.17.
 */
@Getter
@Setter
@AllArgsConstructor
public class RaceGPSCommandEvent {

  private final Message message;

}
