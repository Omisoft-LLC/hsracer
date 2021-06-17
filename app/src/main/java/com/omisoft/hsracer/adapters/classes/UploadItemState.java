package com.omisoft.hsracer.adapters.classes;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by developer on 07.11.17.
 */
@Getter
@Setter
@ToString
public class UploadItemState implements Parcelable {

  private String videoName;
  private String fullPathToVideo;
  private boolean checked;
  private boolean playing;
  private boolean uploading;
  private long raceID;

  public UploadItemState() {
  }

  protected UploadItemState(Parcel in) {
    videoName = in.readString();
    fullPathToVideo = in.readString();
    checked = in.readByte() != 0;
    playing = in.readByte() != 0;
    uploading = in.readByte() != 0;
    raceID = in.readLong();
  }

  public static final Creator<UploadItemState> CREATOR = new Creator<UploadItemState>() {
    @Override
    public UploadItemState createFromParcel(Parcel in) {
      return new UploadItemState(in);
    }

    @Override
    public UploadItemState[] newArray(int size) {
      return new UploadItemState[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(videoName);
    dest.writeString(fullPathToVideo);
    dest.writeByte((byte) (checked ? 1 : 0));
    dest.writeByte((byte) (playing ? 1 : 0));
    dest.writeByte((byte) (uploading ? 1 : 0));
    dest.writeLong(raceID);
  }
}
