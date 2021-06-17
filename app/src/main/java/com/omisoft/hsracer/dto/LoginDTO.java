package com.omisoft.hsracer.dto;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Getter;
import lombok.Setter;


/**
 * Login response
 * Created by dido on 06.04.17.
 */
@Getter
@Setter
public class LoginDTO implements Parcelable {

  public static final Creator<LoginDTO> CREATOR = new Creator<LoginDTO>() {
    @Override
    public LoginDTO createFromParcel(Parcel in) {
      return new LoginDTO(in);
    }

    @Override
    public LoginDTO[] newArray(int size) {
      return new LoginDTO[size];
    }
  };
  private String email;
  private String password;
  private String fireBaseToken;

  public LoginDTO() {

  }

  public LoginDTO(Parcel in) {
    email = in.readString();
    password = in.readString();
    fireBaseToken = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(email);
    dest.writeString(password);
    dest.writeString(fireBaseToken);

  }
}
