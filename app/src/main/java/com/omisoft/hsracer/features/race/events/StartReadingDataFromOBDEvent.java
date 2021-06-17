package com.omisoft.hsracer.features.race.events;

import android.os.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by developer on 04.08.17.
 */
@Getter
@Setter
@AllArgsConstructor

public class StartReadingDataFromOBDEvent {

  private final Message message;

}
