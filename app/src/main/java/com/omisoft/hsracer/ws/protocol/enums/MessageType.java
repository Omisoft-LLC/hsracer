package com.omisoft.hsracer.ws.protocol.enums;

import lombok.ToString;

/**
 * Created by nslavov on 5/17/17.
 */
@ToString
public enum MessageType {
  NEW_RACE(1),
  JOIN_RACE(2),
  FINISH(3),
  CANCEL(4),
  CREATED(5),
  RACER_DATA(6),
  JOINED(7),
  READY(8),
  START_RACE(9),
  CANCEL_RACE(10),
  RANKING(11),
  RACE_STATUS(12),
  FORCE_RACE(13),
  RACE_RESULT(14),
  DEFAULT(15),
  ERROR(16),
  JOIN_FINDER(17),
  PULL_RACERS(18),
  FINDER_RESULT(19),
  PULL_RESULT(20),
  MESSAGE(21),
  KICK_RACER(22);

  public int getCode() {
    return code;
  }


  private final int code;

  MessageType(int code) {
    this.code = code;
  }

  public static MessageType fromCode(int inputCode) {
    for (MessageType t : values()) {
      if (t.getCode() == inputCode) {
        return t;
      }
    }
    return null;
  }
}
