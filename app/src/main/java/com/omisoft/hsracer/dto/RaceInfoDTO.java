package com.omisoft.hsracer.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.omisoft.hsracer.ws.protocol.enums.RaceType;
import lombok.Getter;
import lombok.Setter;

/**
 * Holds race information, used in preSummary activity
 * Created by dido on 22.06.17.
 */
@Getter
@Setter
public class RaceInfoDTO implements Parcelable {

  private int distance;
  private int startSpeed;
  private int endSpeed;
  private RaceType raceType;
  private long creatorId;
  private int countRacers;
  private String name;
  private String description;
  private double finishLat;
  private double finishLng;
  private String finishAddress;

  public RaceInfoDTO() {

  }


  protected RaceInfoDTO(Parcel in) {
    distance = in.readInt();
    startSpeed = in.readInt();
    endSpeed = in.readInt();
    raceType = RaceType.valueOf(in.readString());
    creatorId = in.readLong();
    countRacers = in.readInt();
    name = in.readString();
    description = in.readString();
    finishLat = in.readDouble();
    finishLng = in.readDouble();
    finishAddress = in.readString();
  }

  public static final Creator<RaceInfoDTO> CREATOR = new Creator<RaceInfoDTO>() {
    @Override
    public RaceInfoDTO createFromParcel(Parcel in) {
      return new RaceInfoDTO(in);
    }

    @Override
    public RaceInfoDTO[] newArray(int size) {
      return new RaceInfoDTO[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(distance);
    dest.writeInt(startSpeed);
    dest.writeInt(endSpeed);
    dest.writeString(raceType.name());
    dest.writeLong(creatorId);
    dest.writeInt(countRacers);
    dest.writeString(name);
    dest.writeString(description);
    dest.writeDouble(finishLat);
    dest.writeDouble(finishLng);
    dest.writeString(finishAddress);
  }
}
