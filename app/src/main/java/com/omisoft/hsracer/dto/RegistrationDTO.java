package com.omisoft.hsracer.dto;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Getter;
import lombok.Setter;

/**
 * Registration DTO
 * Created by dido on 06.04.17.
 */
@Getter
@Setter
public class RegistrationDTO implements Parcelable {

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    public RegistrationDTO createFromParcel(Parcel in) {
      return new RegistrationDTO(in);
    }

    public RegistrationDTO[] newArray(int size) {
      return new RegistrationDTO[size];
    }
  };
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String nickName;

  public RegistrationDTO(Parcel in) {
    firstName = in.readString();
    lastName = in.readString();
    email = in.readString();
    password = in.readString();
    nickName = in.readString();
  }

  public RegistrationDTO() {

  }

  public RegistrationDTO(String firstName, String lastName, String email, String password,
      String nickName) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.nickName = nickName;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(firstName);
    dest.writeString(lastName);
    dest.writeString(email);
    dest.writeString(password);
    dest.writeString(nickName);

  }
}
