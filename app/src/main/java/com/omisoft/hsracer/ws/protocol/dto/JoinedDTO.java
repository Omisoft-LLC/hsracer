package com.omisoft.hsracer.ws.protocol.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.omisoft.hsracer.ws.protocol.enums.RaceType;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Joined DTO
 * Created by nslavov on 5/15/17.
 */
@Getter
@Setter
@ToString
public class JoinedDTO implements Parcelable {

  private Long raceId;
  private RaceType raceType;
  private int countRacers;
  private int distance;
  private int startSpeed;
  private int endSpeed;
  private String carName;

  private ArrayList<RacerSummary> summaryList;
  private double finishLat;
  private double finishLng;
  private String finishAddress;

  public JoinedDTO() {
    summaryList = new ArrayList<>();
  }


  protected JoinedDTO(Parcel in) {
    raceId = in.readLong();
    raceType = RaceType.valueOf(in.readString());
    countRacers = in.readInt();
    distance = in.readInt();
    startSpeed = in.readInt();
    endSpeed = in.readInt();
    carName = in.readString();
    summaryList = in.createTypedArrayList(RacerSummary.CREATOR);
    finishLat = in.readDouble();
    finishLng = in.readDouble();
    finishAddress = in.readString();
  }

  public static final Creator<JoinedDTO> CREATOR = new Creator<JoinedDTO>() {
    @Override
    public JoinedDTO createFromParcel(Parcel in) {
      return new JoinedDTO(in);
    }

    @Override
    public JoinedDTO[] newArray(int size) {
      return new JoinedDTO[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(raceId);
    dest.writeString(raceType.name());
    dest.writeInt(countRacers);
    dest.writeInt(distance);
    dest.writeInt(startSpeed);
    dest.writeInt(endSpeed);
    dest.writeString(carName);
    dest.writeTypedList(summaryList);
    dest.writeDouble(finishLat);
    dest.writeDouble(finishLng);
    dest.writeString(finishAddress);
  }


}
