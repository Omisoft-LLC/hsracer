package com.omisoft.hsracer.features.login.events;

import com.omisoft.hsracer.common.BaseEvent;
import com.omisoft.hsracer.dto.LoginResponseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * Login event
 * Created by dido on 13.06.17.
 */
@Getter
@Setter
public class LoginEvent extends BaseEvent {

  private LoginResponseDTO loginResponseDTO;

  public LoginEvent(LoginResponseDTO login, String from, String... to) {
    super(from, to);
    this.loginResponseDTO = login;
  }
}
