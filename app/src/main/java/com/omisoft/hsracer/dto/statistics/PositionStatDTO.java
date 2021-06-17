package com.omisoft.hsracer.dto.statistics;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Data;

@Data
public class PositionStatDTO implements Parcelable {

  private Long profilerestid;
  private Integer _1;
  private Integer _2;
  private Integer _3;
  private Integer _4;
  private Integer _5;
  private Integer _6;
  private Integer _7;
  private Integer _8;

  public PositionStatDTO() {

  }

  protected PositionStatDTO(Parcel in) {
    if (in.readByte() == 0) {
      profilerestid = null;
    } else {
      profilerestid = in.readLong();
    }
    if (in.readByte() == 0) {
      _1 = null;
    } else {
      _1 = in.readInt();
    }
    if (in.readByte() == 0) {
      _2 = null;
    } else {
      _2 = in.readInt();
    }
    if (in.readByte() == 0) {
      _3 = null;
    } else {
      _3 = in.readInt();
    }
    if (in.readByte() == 0) {
      _4 = null;
    } else {
      _4 = in.readInt();
    }
    if (in.readByte() == 0) {
      _5 = null;
    } else {
      _5 = in.readInt();
    }
    if (in.readByte() == 0) {
      _6 = null;
    } else {
      _6 = in.readInt();
    }
    if (in.readByte() == 0) {
      _7 = null;
    } else {
      _7 = in.readInt();
    }
    if (in.readByte() == 0) {
      _8 = null;
    } else {
      _8 = in.readInt();
    }
  }

  public static final Creator<PositionStatDTO> CREATOR = new Creator<PositionStatDTO>() {
    @Override
    public PositionStatDTO createFromParcel(Parcel in) {
      return new PositionStatDTO(in);
    }

    @Override
    public PositionStatDTO[] newArray(int size) {
      return new PositionStatDTO[size];
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
    if (profilerestid == null) {
      dest.writeByte((byte) 0);
    } else {
      dest.writeByte((byte) 1);
      dest.writeLong(profilerestid);
    }
    if (_1 == null) {
      dest.writeByte((byte) 0);
    } else {
      dest.writeByte((byte) 1);
      dest.writeInt(_1);
    }
    if (_2 == null) {
      dest.writeByte((byte) 0);
    } else {
      dest.writeByte((byte) 1);
      dest.writeInt(_2);
    }
    if (_3 == null) {
      dest.writeByte((byte) 0);
    } else {
      dest.writeByte((byte) 1);
      dest.writeInt(_3);
    }
    if (_4 == null) {
      dest.writeByte((byte) 0);
    } else {
      dest.writeByte((byte) 1);
      dest.writeInt(_4);
    }
    if (_5 == null) {
      dest.writeByte((byte) 0);
    } else {
      dest.writeByte((byte) 1);
      dest.writeInt(_5);
    }
    if (_6 == null) {
      dest.writeByte((byte) 0);
    } else {
      dest.writeByte((byte) 1);
      dest.writeInt(_6);
    }
    if (_7 == null) {
      dest.writeByte((byte) 0);
    } else {
      dest.writeByte((byte) 1);
      dest.writeInt(_7);
    }
    if (_8 == null) {
      dest.writeByte((byte) 0);
    } else {
      dest.writeByte((byte) 1);
      dest.writeInt(_8);
    }
  }
}
