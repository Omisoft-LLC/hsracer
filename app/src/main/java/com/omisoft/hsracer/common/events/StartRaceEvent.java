package com.omisoft.hsracer.common.events;

import com.omisoft.hsracer.common.BaseEvent;

/**
 * Start race
 * Created by dido on 11.08.17.
 */

public class StartRaceEvent extends BaseEvent {

  public StartRaceEvent(String from, String... to) {
    super(from, to);
  }
}
