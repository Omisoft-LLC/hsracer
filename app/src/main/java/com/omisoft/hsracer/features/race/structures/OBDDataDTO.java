package com.omisoft.hsracer.features.race.structures;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by dido on 14.08.17.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OBDDataDTO implements Parcelable {

  private int obdSpeed;
  private int obdRpm;
  private int engineCoolantTemp;

  protected OBDDataDTO(Parcel in) {
    obdSpeed = in.readInt();
    obdRpm = in.readInt();
    engineCoolantTemp = in.readInt();
  }

  public static final Creator<OBDDataDTO> CREATOR = new Creator<OBDDataDTO>() {
    @Override
    public OBDDataDTO createFromParcel(Parcel in) {
      return new OBDDataDTO(in);
    }

    @Override
    public OBDDataDTO[] newArray(int size) {
      return new OBDDataDTO[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(obdSpeed);
    dest.writeInt(obdRpm);
    dest.writeInt(engineCoolantTemp);
  }
}
