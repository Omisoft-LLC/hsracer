package com.omisoft.hsracer.ws.protocol;

import com.omisoft.hsracer.ws.protocol.enums.MessageType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by nslavov on 5/12/17.
 */
@Getter
@Setter
@ToString
public class RaceMessage {

  private MessageType type;
  private Object payload;

  public RaceMessage(MessageType type, Object payload) {
    this.type = type;
    this.payload = payload;
  }

  public RaceMessage() {
  }

}
