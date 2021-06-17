package com.omisoft.hsracer.common.events;

import com.omisoft.hsracer.common.BaseEvent;
import lombok.Getter;

/**
 * Generic error,
 * Just log it
 * Created by dido on 16.08.17.
 */
@Getter
public class ErrorEvent extends BaseEvent {

  private Throwable t;
  private final int stringResource;


  public ErrorEvent(int stringResource, Throwable e, String from, String... to) {
    super(from, to);
    t = e;
    this.stringResource = stringResource;
  }

  public ErrorEvent(int stringResource, String from, String... to) {
    super(from, to);
    t = new Exception();
    this.stringResource = stringResource;
  }


}
