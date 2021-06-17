package com.omisoft.hsracer.ws.protocol.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Join a Race
 * Created by nslavov on 5/15/17.
 */
@Getter
@Setter
@ToString
public class JoinRaceDTO {

  private Long raceId;
  private Long profileRestId;
  private String alias;
  private Long carRestId;
  private boolean creator;

  public JoinRaceDTO() {
  }

}
