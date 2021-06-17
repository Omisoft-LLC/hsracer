package com.omisoft.hsracer.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Generic server response DTO
 * Created by Omisoft LLC. on 4/11/17.
 */
@Getter
@Setter
public class DataFromServerDTO {

  private int responseCode;
  private String body;
  private String authId;
  private String aesKey;

}
