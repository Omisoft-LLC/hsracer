package com.omisoft.hsracer.features.race.structures;

import android.os.Parcel;
import android.os.Parcelable;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.events.ErrorEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.greenrobot.eventbus.EventBus;

/**
 * DTO, holds race data
 * Created by dido on 11.08.17.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class RaceDataDTO implements Cloneable, Parcelable {

  private int gpsSpeed;
  private int gpsAcceleration;
  private long raceCurrentTime;
  private int gpsDistance;

  private int obdRpm;
  private int obdSpeed;
  private int engineCoolantTemp;
  private double longitude;
  private double latitude;


  protected RaceDataDTO(Parcel in) {
    gpsSpeed = in.readInt();
    gpsAcceleration = in.readInt();
    raceCurrentTime = in.readLong();
    gpsDistance = in.readInt();
    obdRpm = in.readInt();
    obdSpeed = in.readInt();
    engineCoolantTemp = in.readInt();
    longitude = in.readDouble();
    latitude = in.readDouble();


  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(gpsSpeed);
    dest.writeInt(gpsAcceleration);
    dest.writeLong(raceCurrentTime);
    dest.writeInt(gpsDistance);
    dest.writeInt(obdRpm);
    dest.writeInt(obdSpeed);
    dest.writeInt(engineCoolantTemp);
    dest.writeDouble(longitude);
    dest.writeDouble(latitude);

  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Creator<RaceDataDTO> CREATOR = new Creator<RaceDataDTO>() {
    @Override
    public RaceDataDTO createFromParcel(Parcel in) {
      return new RaceDataDTO(in);
    }

    @Override
    public RaceDataDTO[] newArray(int size) {
      return new RaceDataDTO[size];
    }
  };

  @Override
  public RaceDataDTO clone() {
    try {
      return (RaceDataDTO) super.clone();
    } catch (CloneNotSupportedException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_generic, e, this.getClass().getSimpleName()));

      throw new AssertionError(); // Can't happen
    }
  }
}
