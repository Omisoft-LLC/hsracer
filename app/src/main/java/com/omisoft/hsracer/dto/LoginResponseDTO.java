package com.omisoft.hsracer.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Login Response DTO
 * Created by Omisoft LLC. on 6/7/17.
 */

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponseDTO
    implements Parcelable {

  public static final Creator<LoginResponseDTO> CREATOR = new Creator<LoginResponseDTO>() {
    @Override
    public LoginResponseDTO createFromParcel(Parcel in) {
      return new LoginResponseDTO(in);
    }

    @Override
    public LoginResponseDTO[] newArray(int size) {
      return new LoginResponseDTO[size];
    }
  };
  private String email;
  private String password;
  private String auth;
  private Long restId;
  private String nickname;
  private String apiKey;
  private String aesKey;

  public LoginResponseDTO() {
  }

  protected LoginResponseDTO(Parcel in) {
    email = in.readString();
    password = in.readString();
    auth = in.readString();
    restId = in.readLong();
    nickname = in.readString();
    apiKey = in.readString();
    aesKey = in.readString();
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(email);
    dest.writeString(password);
    dest.writeString(auth);
    dest.writeLong(restId);
    dest.writeString(nickname);
    dest.writeString(apiKey);
    dest.writeString(aesKey);
  }
}
