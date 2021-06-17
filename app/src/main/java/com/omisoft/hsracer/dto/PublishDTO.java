package com.omisoft.hsracer.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.omisoft.hsracer.features.race.structures.RaceDataDTO;
import com.omisoft.hsracer.ws.protocol.enums.PlayerStatus;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Publish DTO, used while sending info for publishing
 * Created by nslavov on 5/22/17.
 */
@Getter
@Setter
@ToString
public class PublishDTO implements Parcelable {

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    public PublishDTO createFromParcel(Parcel in) {
      return new PublishDTO(in);
    }

    public PublishDTO[] newArray(int size) {
      return new PublishDTO[size];
    }
  };

  private Long raceId;
  private PlayerStatus playerStatus;
  private Long carRestId;
  private String carName;
  private String alias;
  private Long profileRestId;
  private Long raceTime = 0L;
  private Integer avgSpeed = 0;
  private Integer maxSpeed = 0;
  private Integer position;
  private ArrayList<RaceDataDTO> raceDataDTOList = new ArrayList<>();


  public PublishDTO() {

  }

  public PublishDTO(Parcel in) {
    raceId = in.readLong();
    playerStatus = PlayerStatus.valueOf(in.readString());
    carRestId = in.readLong();
    carName = in.readString();
    alias = in.readString();
    profileRestId = in.readLong();
    raceTime = in.readLong();
    avgSpeed = in.readInt();
    maxSpeed = in.readInt();
    position = in.readInt();
    raceDataDTOList = in.createTypedArrayList(RaceDataDTO.CREATOR);

  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(raceId);
    dest.writeString(playerStatus.name());
    dest.writeLong(carRestId);
    dest.writeString(carName);
    dest.writeString(alias);
    dest.writeLong(profileRestId);
    dest.writeLong(raceTime);
    dest.writeInt(avgSpeed);
    dest.writeInt(maxSpeed);
    dest.writeInt(position);
    dest.writeTypedList(raceDataDTOList);

  }
}
