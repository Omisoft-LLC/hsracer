package com.omisoft.hsracer.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.omisoft.hsracer.ws.protocol.enums.RaceType;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Race results
 * Created by Omisoft LLC. on 6/6/17.
 */
@ToString
@Getter
@Setter
public class RaceResultDTO implements Parcelable {

  public static final Creator<RaceResultDTO> CREATOR = new Creator<RaceResultDTO>() {
    @Override
    public RaceResultDTO createFromParcel(Parcel in) {
      return new RaceResultDTO(in);
    }

    @Override
    public RaceResultDTO[] newArray(int size) {
      return new RaceResultDTO[size];
    }
  };
  private Long raceRestId;
  private String name;
  private Integer raceDistance;
  private Long raceTimeInMills;
  private RaceType raceType;
  private int finishPosition;
  private Float maxSpeed;
  private Date startDate;
  private Date endDate;
  private String shareURL;
  private String description;

  public RaceResultDTO() {
  }

  protected RaceResultDTO(Parcel in) {
    raceRestId = in.readLong();
    name = in.readString();
    raceDistance = in.readInt();
    raceTimeInMills = in.readLong();
    raceType = RaceType.valueOf(in.readString());
    finishPosition = in.readInt();
    maxSpeed = in.readFloat();
    startDate = new Date(in.readLong());
    endDate = new Date(in.readLong());
    shareURL = in.readString();
    description = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(String.valueOf(raceRestId));
    dest.writeString(name);
    dest.writeInt(raceDistance != null ? raceDistance : 0);
    dest.writeLong(raceTimeInMills != null ? raceTimeInMills : 0);

    dest.writeString(raceType.name());
    dest.writeInt(finishPosition);
    dest.writeFloat(maxSpeed != null ? maxSpeed : 0f);

    dest.writeLong(startDate != null ? startDate.getTime() : 0);

    dest.writeLong(endDate != null ? endDate.getTime() : 0L);

    dest.writeString(shareURL);
    dest.writeString(description);
  }
}
