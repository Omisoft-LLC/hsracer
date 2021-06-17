package com.omisoft.hsracer.common.events;

import com.omisoft.hsracer.common.BaseEvent;
import lombok.Getter;

/**
 * Progress bar event
 * Created by dido on 05.09.17.
 */
@Getter
public class ProgressBarEvent extends BaseEvent {

  private final int visibility;

  public ProgressBarEvent(int visibility, String from, String... to) {
    super(from, to);
    this.visibility = visibility;
  }
}
