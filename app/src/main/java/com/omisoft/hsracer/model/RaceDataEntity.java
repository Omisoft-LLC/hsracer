package com.omisoft.hsracer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;

/**
 * Location data points. A gps point in time
 * Created by dido on 21.04.17.
 */

@Entity(tableName = "RACE_DATA", indices = {@Index("race_id")})
public class RaceDataEntity extends BaseEntity {

  public Long getCurrentTime() {
    return currentTime;
  }

  public void setCurrentTime(Long currentTime) {
    this.currentTime = currentTime;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Integer getGpsSpeed() {
    return gpsSpeed;
  }

  public void setGpsSpeed(Integer gpsSpeed) {
    this.gpsSpeed = gpsSpeed;
  }

  public Integer getGpsDistance() {
    return gpsDistance;
  }

  public void setGpsDistance(Integer gpsDistance) {
    this.gpsDistance = gpsDistance;
  }

  public Integer getGpsAcceleration() {
    return gpsAcceleration;
  }

  public void setGpsAcceleration(Integer gpsAcceleration) {
    this.gpsAcceleration = gpsAcceleration;
  }

  public Integer getObdRpm() {
    return obdRpm;
  }

  public void setObdRpm(Integer obdRpm) {
    this.obdRpm = obdRpm;
  }

  public Integer getObdSpeed() {
    return obdSpeed;
  }

  public void setObdSpeed(Integer obdSpeed) {
    this.obdSpeed = obdSpeed;
  }

  public Long getRaceId() {
    return raceId;
  }

  public void setRaceId(Long raceId) {
    this.raceId = raceId;
  }

  public Long getRestId() {
    return restId;
  }

  public void setRestId(Long restId) {
    this.restId = restId;
  }

  private Long currentTime;
  private Double latitude;
  private Double longitude;
  private Integer gpsSpeed;
  private Integer gpsDistance;
  private Integer gpsAcceleration;
  private Integer obdRpm;
  private Integer obdSpeed;
  @ColumnInfo(name = "race_id")
  private Long raceId;
  @ColumnInfo(name = "rest_id")
  private Long restId;


}
