package com.omisoft.hsracer.features.buddyfinder.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Send Message  to WS
 * Created by nslavov on 6/13/17.
 */
@Getter
@Setter
public class SendMessageDTO {

  private Long restIdFrom;
  private Long restIdTo;
  private String message;

  public SendMessageDTO() {
  }


}
