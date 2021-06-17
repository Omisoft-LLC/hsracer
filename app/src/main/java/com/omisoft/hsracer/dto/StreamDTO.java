package com.omisoft.hsracer.dto;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Live Stream DTO
 * Created by dido on 04.05.17.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StreamDTO implements Parcelable {

  private String hls;
  private String dash;
  private String nickname;
  private Long raceId;
  private String raceDesc;
  private Date startStreamAt;
  private String streamkey;

  protected StreamDTO(Parcel in) {
    hls = in.readString();
    dash = in.readString();
    nickname = in.readString();
    if (in.readByte() == 0) {
      raceId = null;
    } else {
      raceId = in.readLong();
    }
    raceDesc = in.readString();
    streamkey = in.readString();
  }

  public static final Creator<StreamDTO> CREATOR = new Creator<StreamDTO>() {
    @Override
    public StreamDTO createFromParcel(Parcel in) {
      return new StreamDTO(in);
    }

    @Override
    public StreamDTO[] newArray(int size) {
      return new StreamDTO[size];
    }
  };

  /**
   * Describe the kinds of special objects contained in this Parcelable
   * instance's marshaled representation. For example, if the object will
   * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
   * the return value of this method must include the
   * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
   *
   * @return a bitmask indicating the set of special object types marshaled by this Parcelable
   * object instance.
   */
  @Override
  public int describeContents() {
    return 0;
  }

  /**
   * Flatten this object in to a Parcel.
   *
   * @param dest The Parcel in which the object should be written.
   * @param flags Additional flags about how the object should be written. May be 0 or {@link
   * #PARCELABLE_WRITE_RETURN_VALUE}.
   */
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(hls);
    dest.writeString(dash);
    dest.writeString(nickname);
    if (raceId == null) {
      dest.writeByte((byte) 0);
    } else {
      dest.writeByte((byte) 1);
      dest.writeLong(raceId);
    }
    dest.writeString(raceDesc);
    dest.writeString(streamkey);
  }
}
