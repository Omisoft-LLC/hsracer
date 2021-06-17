package com.omisoft.hsracer.features.profile.events;

import com.omisoft.hsracer.common.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dido on 13.06.17.
 */
@Getter
@AllArgsConstructor

public class SuccessCreateCarEvent extends BaseEvent {

  private final Long restId;


  public SuccessCreateCarEvent(Long restId, String from, String... to) {
    super(from, to);
    this.restId = restId;
  }
}
