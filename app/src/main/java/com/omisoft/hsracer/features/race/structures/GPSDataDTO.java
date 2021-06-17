package com.omisoft.hsracer.features.race.structures;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * GPS Data holder
 * Created by dido on 14.08.17.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class GPSDataDTO implements Parcelable {

  private int speed;
  private int acceleration;
  private long currentTime;
  private int distance;
  private double longitude;
  private double latitude;

  protected GPSDataDTO(Parcel in) {
    speed = in.readInt();
    acceleration = in.readInt();
    currentTime = in.readLong();
    distance = in.readInt();
    longitude = in.readDouble();
    latitude = in.readDouble();
  }

  public static final Creator<GPSDataDTO> CREATOR = new Creator<GPSDataDTO>() {
    @Override
    public GPSDataDTO createFromParcel(Parcel in) {
      return new GPSDataDTO(in);
    }

    @Override
    public GPSDataDTO[] newArray(int size) {
      return new GPSDataDTO[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(speed);
    dest.writeInt(acceleration);
    dest.writeLong(currentTime);
    dest.writeInt(distance);
    dest.writeDouble(longitude);
    dest.writeDouble(latitude);
    dest.writeDouble(latitude);
  }
}
