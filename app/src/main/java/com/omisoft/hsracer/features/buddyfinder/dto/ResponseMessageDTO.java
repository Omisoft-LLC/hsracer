package com.omisoft.hsracer.features.buddyfinder.dto;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by nslavov on 6/13/17.
 */
@Getter
@Setter
public class ResponseMessageDTO implements Parcelable {

  private Long restIdFrom;
  private String fromNickName;
  //  private Long restIdTo;
//  private String toNickName;
  private String message;

  public ResponseMessageDTO() {
  }

  public ResponseMessageDTO(Long restIdFrom, String fromNickName, String message) {
    this.restIdFrom = restIdFrom;
    this.fromNickName = fromNickName;
    this.message = message;
  }

  protected ResponseMessageDTO(Parcel in) {
    fromNickName = in.readString();
    message = in.readString();
    restIdFrom = in.readLong();
  }

  public static final Creator<ResponseMessageDTO> CREATOR = new Creator<ResponseMessageDTO>() {
    @Override
    public ResponseMessageDTO createFromParcel(Parcel in) {
      return new ResponseMessageDTO(in);
    }

    @Override
    public ResponseMessageDTO[] newArray(int size) {
      return new ResponseMessageDTO[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(fromNickName);
    dest.writeString(message);
    dest.writeLong(restIdFrom);
  }
}
