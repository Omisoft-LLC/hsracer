package com.omisoft.hsracer.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Profile DTO
 * Created by Omisoft LLC. on 6/5/17.
 */
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDTO implements Parcelable {

  private String nickName;
  private String firstName;
  private String lastName;
  private Short age;
  private String city;
  private String country;
  private String authorizationId;

  public ProfileDTO() {
  }


  protected ProfileDTO(Parcel in) {
    nickName = in.readString();
    firstName = in.readString();
    lastName = in.readString();
    age = (short) in.readInt();
    city = in.readString();
    country = in.readString();
    authorizationId = in.readString();
  }

  public static final Creator<ProfileDTO> CREATOR = new Creator<ProfileDTO>() {
    @Override
    public ProfileDTO createFromParcel(Parcel in) {
      return new ProfileDTO(in);
    }

    @Override
    public ProfileDTO[] newArray(int size) {
      return new ProfileDTO[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(nickName);
    dest.writeString(firstName);
    dest.writeString(lastName);
    dest.writeInt(age);
    dest.writeString(city);
    dest.writeString(country);
    dest.writeString(authorizationId);
  }
}