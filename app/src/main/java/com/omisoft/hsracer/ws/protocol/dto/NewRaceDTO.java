package com.omisoft.hsracer.ws.protocol.dto;

import com.omisoft.hsracer.ws.protocol.enums.RaceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * New Race
 * Created by nslavov on 5/15/17.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class NewRaceDTO {

  private RaceType raceType;
  private Long creatorId;
  private int countRacers;
  private Integer distance;
  private Integer startSpeed;
  private Integer endSpeed;
  private String name;
  private String description;
  private double finishLat;
  private double finishLng;
  private String finishAddress;


  public NewRaceDTO(
      RaceType raceType, int countRacers, Integer distance, Integer startSpeed, Integer endSpeed) {
    this.raceType = raceType;
    this.countRacers = countRacers;
    this.distance = distance;
    this.startSpeed = startSpeed;
    this.endSpeed = endSpeed;
  }
}
