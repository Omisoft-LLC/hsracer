package com.omisoft.hsracer.dto;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Share results DTO
 * Created by dido on 07.07.17.
 */
@Getter
@Setter
@ToString
public class PublishResultDTO implements Parcelable {

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    public PublishResultDTO createFromParcel(Parcel in) {
      return new PublishResultDTO(in);
    }

    public PublishResultDTO[] newArray(int size) {
      return new PublishResultDTO[size];
    }
  };

  private String shareUrl;
  private Long racingRestId;

  public PublishResultDTO() {

  }

  public PublishResultDTO(Parcel in) {
    shareUrl = in.readString();
    racingRestId = in.readLong();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(shareUrl);
    dest.writeLong(racingRestId);
  }
}
