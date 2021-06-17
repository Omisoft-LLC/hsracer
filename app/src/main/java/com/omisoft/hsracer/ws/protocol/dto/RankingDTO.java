package com.omisoft.hsracer.ws.protocol.dto;

import com.omisoft.hsracer.ws.protocol.enums.PlayerStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by nslavov on 5/18/17.
 */
@Getter
@Setter
@ToString
public class RankingDTO {

  private String personalId;
  private Integer s;
  private Integer v;
  private Double t;
  private Integer avgSpeed;
  private Integer maxSpeed;
  private Integer distance;
  private Integer currentSpeed;
  private String alias;
  private PlayerStatus playerStatus;

  public RankingDTO() {
  }
}
