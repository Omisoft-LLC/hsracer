package com.omisoft.hsracer.features.profile.events;

import com.omisoft.hsracer.common.BaseEvent;
import lombok.AllArgsConstructor;

/**
 * Success updating profile
 * Created by Omisoft LLC. on 6/19/17.
 */
@AllArgsConstructor
public class SuccessUpdateProfileEvent extends BaseEvent {

  public SuccessUpdateProfileEvent(String from, String... to) {
    super(from, to);
  }


}
