package com.omisoft.hsracer.features.login.events;

import com.omisoft.hsracer.common.BaseEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * Login failed event
 * Created by dido on 13.06.17.
 */
@Getter
@Setter
public class LoginFailedEvent extends BaseEvent {

  private String errorMessage;

  public LoginFailedEvent(String from, String errorMessage, String... to) {
    super(from, to);
    this.errorMessage = errorMessage;
  }
}
