package com.omisoft.hsracer.dto;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Omisoft LLC. on 5/18/17.
 */
@Getter
@Setter
public class SuccessDTO implements Parcelable {

  public static final Creator<SuccessDTO> CREATOR = new Creator<SuccessDTO>() {
    @Override
    public SuccessDTO createFromParcel(Parcel in) {
      return new SuccessDTO(in);
    }

    @Override
    public SuccessDTO[] newArray(int size) {
      return new SuccessDTO[size];
    }
  };
  private String message;


  public SuccessDTO() {
  }

  public SuccessDTO(Parcel in) {
    message = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(message);

  }
}
