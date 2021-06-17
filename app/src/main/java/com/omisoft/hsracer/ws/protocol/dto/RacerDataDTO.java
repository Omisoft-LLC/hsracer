package com.omisoft.hsracer.ws.protocol.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data sent to server
 * Created by nslavov on 5/15/17.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RacerDataDTO {

  private Long raceId;
  private Long profileRestId;
  private Integer s = 0;
  private Integer v = 0;
  private Long t = 0L;
  private Integer maxSpeed = 0;
  private Integer avgSpeed = 0;
  private Integer currentSpeed = 0;
  private Integer currentDistance = 0;
  private Double currentLat = 0D;
  private Double currentLng = 0D;
  private boolean isLive;
  private String streamerApiKey;


}