package com.omisoft.hsracer.features.buddyfinder.dto;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Omisoft LLC. on 6/21/17.
 */
@Getter
@Setter
public class LeaveFinderDTO implements Parcelable {

  private Long profileRestId;


  protected LeaveFinderDTO(Parcel in) {
    profileRestId = in.readLong();
  }

  public LeaveFinderDTO() {
  }

  public static final Creator<LeaveFinderDTO> CREATOR = new Creator<LeaveFinderDTO>() {
    @Override
    public LeaveFinderDTO createFromParcel(Parcel in) {
      return new LeaveFinderDTO(in);
    }

    @Override
    public LeaveFinderDTO[] newArray(int size) {
      return new LeaveFinderDTO[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(profileRestId);
  }
}
