package com.omisoft.hsracer.features.profile.events;

import com.omisoft.hsracer.common.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Success car update event
 * Created by dido on 13.06.17.
 */
@Getter
@Setter
@AllArgsConstructor
public class SuccessUpdateCarEvent extends BaseEvent {


  public SuccessUpdateCarEvent(String from, String... to) {
    super(from, to);
  }
}
