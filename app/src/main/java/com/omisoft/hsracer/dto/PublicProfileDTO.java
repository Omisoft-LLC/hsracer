package com.omisoft.hsracer.dto;


import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Public Profile DTO
 * Created by nslavov on 6/1/17.
 */
@Getter
@Setter
@ToString
public class PublicProfileDTO {

  private UUID id;
  private Long restId;
  private String email;
  private String nickName;
  private String firstName;
  private String lastName;
  private Short age;
  private String city;
  private String country;
  private String lastRace;
  private int points;
  private String lastRacePosition;
  private List<CarDTO> cars;
  private byte[] avatar;

  public PublicProfileDTO() {
  }


}
