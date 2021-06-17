package com.omisoft.hsracer.common.events;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by developer on 10.08.17.
 */
@Getter
@Setter
public class MockLocationEvent {
  private final char message;

  public MockLocationEvent(char message) {
    this.message = message;
  }
}
