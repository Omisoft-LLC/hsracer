package com.omisoft.hsracer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.content.ContentValues;

/**
 * Location data points. A gps point in time
 * Created by dido on 21.04.17.
 */

@Entity(tableName = "OBD_DATA", indices = {@Index("race_id")})
public class OBDData extends BaseEntity {

  private Long time;
  private Integer speed;
  private Integer rpm;
  @ColumnInfo(name = "race_id")
  private Long raceId;
  @ColumnInfo(name = "rest_id")
  private Long restId;

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }


  public ContentValues toContentValues() {
    return null;
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

  public Integer getSpeed() {
    return speed;
  }

  public void setSpeed(Integer speed) {
    this.speed = speed;
  }


  public Integer getRpm() {
    return rpm;
  }

  public void setRpm(Integer rpm) {
    this.rpm = rpm;
  }
}
