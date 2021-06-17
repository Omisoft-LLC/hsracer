package com.omisoft.hsracer.dto;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Holds data for cannonball race
 * Created by dido on 23.11.17.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CannonBallAddressInfoDTO implements Parcelable {

  private double latitude;
  private double longitude;
  private String address;

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeDouble(this.latitude);
    dest.writeDouble(this.longitude);
    dest.writeString(this.address);
  }


  protected CannonBallAddressInfoDTO(Parcel in) {
    this.latitude = in.readDouble();
    this.longitude = in.readDouble();
    this.address = in.readString();
  }

  public static final Creator<CannonBallAddressInfoDTO> CREATOR = new Creator<CannonBallAddressInfoDTO>() {
    @Override
    public CannonBallAddressInfoDTO createFromParcel(Parcel source) {
      return new CannonBallAddressInfoDTO(source);
    }

    @Override
    public CannonBallAddressInfoDTO[] newArray(int size) {
      return new CannonBallAddressInfoDTO[size];
    }
  };
}
