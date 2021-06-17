package com.omisoft.hsracer.ws.protocol.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Race Created DTO
 * Created by nslavov on 5/15/17.
 */
@Getter
@Setter
@ToString
public class RaceCreatedDTO {

  private Long raceId;
  private int countRacers;

  public RaceCreatedDTO() {
  }

  public RaceCreatedDTO(Long raceId, int countRacers) {
    this.countRacers = countRacers;
    this.raceId = raceId;
  }

}
