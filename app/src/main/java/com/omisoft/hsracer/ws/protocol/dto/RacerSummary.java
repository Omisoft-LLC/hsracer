package com.omisoft.hsracer.ws.protocol.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.omisoft.hsracer.ws.protocol.enums.PlayerStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by nslavov on 5/17/17.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@JsonInclude(Include.NON_NULL)
public class RacerSummary implements Parcelable, Comparable<RacerSummary> {
  public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    public RacerSummary createFromParcel(Parcel in) {
      return new RacerSummary(in);
    }

    public RacerSummary[] newArray(int size) {
      return new RacerSummary[size];
    }
  };
  private PlayerStatus playerStatus;
  private Long carRestId;
  private String carName;
  private String alias;
  private Long profileRestId;
  private Integer s = 0;
  private Integer v = 0;
  private Long t = 0L;
  private Integer avgSpeed = 0;
  private Integer maxSpeed = 0;
  private Integer currentDistance = 0;
  private Integer currentSpeed = 0;
  private Integer position;
  private int id;
  private String racerNickname;
  private Double currentLat = 0D;
  private Double currentLng = 0D;
  private boolean creator;
  private boolean isLive;
  String streamerApiKey;



  public RacerSummary(Parcel in) {
    alias = in.readString();
    carName = in.readString();
    profileRestId = in.readLong();
    playerStatus = PlayerStatus.valueOf(in.readString());
    s = in.readInt();
    v = in.readInt();
    t = in.readLong();
    avgSpeed = in.readInt();
    maxSpeed = in.readInt();
    currentDistance = in.readInt();
    currentSpeed = in.readInt();
    position = in.readInt();
    racerNickname = in.readString();
    currentLat = in.readDouble();
    currentLng = in.readDouble();
    creator = in.readByte() != 0;
    isLive = in.readByte() != 0;
    streamerApiKey = in.readString();
  }

  public RacerSummary() {
    this.s = 0;
    this.v = 0;
    this.t = 0L;
    this.maxSpeed = 0;
    this.avgSpeed = 0;
    this.currentSpeed = 0;
    this.currentDistance = 0;
    this.currentLat = 0d;
    this.currentLng = 0d;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(alias);
    dest.writeString(carName);
    dest.writeLong(profileRestId);
    dest.writeString(playerStatus.name());

    dest.writeInt(s);
    dest.writeInt(v);
    dest.writeLong(t);
    dest.writeInt(avgSpeed);
    dest.writeInt(maxSpeed);
    dest.writeInt(currentDistance);
    dest.writeInt(currentSpeed);
    dest.writeInt(position);
    dest.writeString(racerNickname);
    dest.writeDouble(currentLat);
    dest.writeDouble(currentLng);
    dest.writeByte((byte) (creator ? 1 : 0));
    dest.writeByte((byte) (isLive ? 1 : 0));
    dest.writeString(streamerApiKey);

  }

  @Override
  public int compareTo(@NonNull RacerSummary o) {
    return this.position.compareTo(o.getPosition());
  }
}
