package com.omisoft.hsracer.common.events;

import com.omisoft.hsracer.common.BaseEvent;

/**
 * Successful send of server error
 * Created by dido on 13.06.17.
 */

public class SuccessSendErrorToServerEvent extends BaseEvent {

  public SuccessSendErrorToServerEvent(String from, String... to) {
    super(from, to);
  }
}
