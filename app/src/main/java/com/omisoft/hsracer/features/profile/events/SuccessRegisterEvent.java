package com.omisoft.hsracer.features.profile.events;

import com.omisoft.hsracer.common.BaseEvent;
import lombok.AllArgsConstructor;

/**
 * Success register event
 * Created by dido on 13.06.17.
 */
@AllArgsConstructor
public class SuccessRegisterEvent extends BaseEvent {

  public SuccessRegisterEvent(String from, String... to) {
    super(from, to);
  }
}
