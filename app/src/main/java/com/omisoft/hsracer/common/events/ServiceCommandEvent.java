package com.omisoft.hsracer.common.events;

import android.os.Message;
import lombok.Getter;
import lombok.Setter;

/**
 * Service command event.
 * Used to start service threads
 * Created by dido on 20.06.17.
 */
@Getter
@Setter
public class ServiceCommandEvent {

  private Message message;

  public ServiceCommandEvent(Message message) {
    this.message = message;
  }

}
