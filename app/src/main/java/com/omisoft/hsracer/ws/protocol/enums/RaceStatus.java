package com.omisoft.hsracer.ws.protocol.enums;

import lombok.ToString;

/**
 * Created by nslavov on 5/17/17.
 */
@ToString
public enum RaceStatus {
  RUNNING(1),
  FINISHED(2),
  CANCELED(3),
  READY(4),
  WAITING(5),
  CREATED(6),
  START_RACE(7),
  RACE_OVER(8);

  private final int code;

  RaceStatus(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public static RaceStatus fromCode(int inputCode) {
    for (RaceStatus t : values()) {
      if (t.getCode() == inputCode) {
        return t;
      }
    }
    return null;
  }
}
