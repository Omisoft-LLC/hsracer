package com.omisoft.hsracer.common.events;

import com.omisoft.hsracer.common.BaseEvent;

/**
 * \Does nothing. Needed for .HomeFragment and its super classes have no public methods with the
 * @Subscribe annotation
 * DO NOT DELETE
 * Created by dido on 20.06.17.
 */

public class NoopEvent extends BaseEvent {


  protected NoopEvent(String from, String... to) {
    super(from, to);
  }
}
