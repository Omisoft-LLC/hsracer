package com.omisoft.hsracer.features.race.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

/**
 * Created by developer on 29.08.17.
 */

@Data
@Setter
@AllArgsConstructor
public class JoinRaceErrorEvent {

  private String message;

}
