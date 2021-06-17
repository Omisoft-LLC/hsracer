package com.omisoft.hsracer.features.profile.events;

import com.omisoft.hsracer.common.BaseEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * Failed register event
 * Created by dido on 13.06.17.
 */
@Getter
@Setter
public class FailedRegisterEvent extends BaseEvent {

  private int message;
  private String messageError;

  public FailedRegisterEvent(int message, String messageError, String from, String... to) {
    super(from, to);
    this.message = message;
    this.messageError = messageError;
  }

}
