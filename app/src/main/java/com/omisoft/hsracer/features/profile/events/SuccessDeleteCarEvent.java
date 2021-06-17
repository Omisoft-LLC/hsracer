package com.omisoft.hsracer.features.profile.events;

import com.omisoft.hsracer.common.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Success register event
 * Created by dido on 13.06.17.
 */
@Getter
@AllArgsConstructor

public class SuccessDeleteCarEvent extends BaseEvent {

  private final int positionInAdapter;

  public SuccessDeleteCarEvent(int positionInAdapter, String from, String... to) {
    super(from, to);
    this.positionInAdapter = positionInAdapter;
  }
}
