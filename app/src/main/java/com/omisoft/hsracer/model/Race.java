package com.omisoft.hsracer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.content.ContentValues;
import android.database.Cursor;
import com.omisoft.hsracer.ws.protocol.enums.RaceType;


/**
 * Race model, holds particular race info for the racer
 * Created by dido on 20.04.17.
 */

@Entity(foreignKeys = {@ForeignKey(entity = Profile.class,
    parentColumns = "_id",
    childColumns = "profile_id"), @ForeignKey(entity = Car.class,
    parentColumns = "_id",
    childColumns = "car_id")}, indices = {@Index("profile_id"), @Index("car_id")})
public class Race extends BaseEntity {

  private String name;
  private String description;
  private String shareUrl;
  // race location
  @Embedded(prefix = "race_")
  private RaceDataEntity raceLocation;
  private RaceType raceType;
  private Integer raceDistance; // in m
  private Long raceTimeInMills;


  @ColumnInfo(name = "car_id")
  private Long carId;
  @Embedded
  private VideoData videoData;
  @Embedded
  private CarTelemetry carTelemetry;
  private Integer finishPosition;
  @ColumnInfo(name = "rest_id")

  private Long restId;

  @ColumnInfo(name = "profile_id")
  public Long profile_id;

  public Race() {

  }

  public Race(Cursor cursor) {

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getShareUrl() {
    return shareUrl;
  }

  public void setShareUrl(String shareUrl) {
    this.shareUrl = shareUrl;
  }

  public RaceDataEntity getRaceLocation() {
    return raceLocation;
  }

  public void setRaceLocation(RaceDataEntity raceLocation) {
    this.raceLocation = raceLocation;
  }

  public RaceType getRaceType() {
    return raceType;
  }

  public void setRaceType(RaceType raceType) {
    this.raceType = raceType;
  }

  public Integer getRaceDistance() {
    return raceDistance;
  }

  public void setRaceDistance(Integer raceDistance) {
    this.raceDistance = raceDistance;
  }

  public Long getRaceTimeInMills() {
    return raceTimeInMills;
  }

  public void setRaceTimeInMills(Long raceTimeInMills) {
    this.raceTimeInMills = raceTimeInMills;
  }


  public VideoData getVideoData() {
    return videoData;
  }

  public void setVideoData(VideoData videoData) {
    this.videoData = videoData;
  }

  public CarTelemetry getCarTelemetry() {
    return carTelemetry;
  }

  public void setCarTelemetry(CarTelemetry carTelemetry) {
    this.carTelemetry = carTelemetry;
  }

  public Integer getFinishPosition() {
    return finishPosition;
  }

  public void setFinishPosition(Integer finishPosition) {
    this.finishPosition = finishPosition;
  }

  public Long getRestId() {
    return restId;
  }

  public void setRestId(Long restId) {
    this.restId = restId;
  }

  public Long getProfile_id() {
    return profile_id;
  }

  public void setProfile_id(Long profile_id) {
    this.profile_id = profile_id;
  }

  public Long getCarId() {
    return carId;
  }

  public void setCarId(Long carId) {
    this.carId = carId;
  }


  public ContentValues toContentValues() {
    ContentValues contentValues = new ContentValues();
    contentValues.put("name", name);
    contentValues.put("description", description);
    contentValues.put("shareUrl", shareUrl);
    contentValues.put("location", raceLocation.getId());
    contentValues.put("raceType", raceType.name());
    contentValues.put("raceDistance", raceDistance);
    contentValues.put("finishPosition", finishPosition);
    return null;
  }
}
