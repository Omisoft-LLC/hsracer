package com.omisoft.hsracer.common.events;

import com.omisoft.hsracer.common.BaseEvent;
import com.omisoft.hsracer.features.race.structures.RaceDataDTO;
import lombok.Getter;

/**
 * Race Data Event
 * Created by dido on 11.08.17.
 */
@Getter
public class RaceDataEvent extends BaseEvent {

  private final RaceDataDTO raceDataDTO;

  public RaceDataEvent(RaceDataDTO raceDataDTO, String from, String... to) {
    super(from, to);
    this.raceDataDTO = raceDataDTO;
  }

}
