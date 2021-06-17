package com.omisoft.hsracer.ws.protocol.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by nslavov on 5/18/17.
 */
@Getter
@Setter
@ToString
public class RaceDTO {

  private Long raceId;
  private Long profileRestId;

  public RaceDTO() {
  }

}
