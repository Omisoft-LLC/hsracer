package com.omisoft.hsracer.features.buddyfinder.dto;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Getter;
import lombok.Setter;

/**
 * Find join race
 *
 * Created by Omisoft LLC. on 6/9/17.
 */


@Getter
@Setter
public class JoinFinderDTO implements Parcelable {

  private Long profileRestId;
  private String nickName;
  private Double latitude;
  private Double longitude;
  private int distance;

  public JoinFinderDTO() {
  }

  protected JoinFinderDTO(Parcel in) {
    nickName = in.readString();
    distance = in.readInt();
    profileRestId = in.readLong();
    latitude = in.readDouble();
    longitude = in.readDouble();
  }

  public static final Creator<JoinFinderDTO> CREATOR = new Creator<JoinFinderDTO>() {
    @Override
    public JoinFinderDTO createFromParcel(Parcel in) {
      return new JoinFinderDTO(in);
    }

    @Override
    public JoinFinderDTO[] newArray(int size) {
      return new JoinFinderDTO[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(nickName);
    dest.writeInt(distance);
    dest.writeLong(profileRestId);
    dest.writeDouble(latitude);
    dest.writeDouble(longitude);
  }
}
