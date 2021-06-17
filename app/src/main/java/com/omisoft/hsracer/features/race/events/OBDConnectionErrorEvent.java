package com.omisoft.hsracer.features.race.events;

import android.os.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by developer on 09.08.17.
 */
@Getter
@Setter
@AllArgsConstructor
public class OBDConnectionErrorEvent {

  private final Message message;

}