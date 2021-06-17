package com.omisoft.hsracer.model;

/**
 * Holds car telemetry for race.
 * TBD
 * Created by dido on 21.04.17.
 */

public class CarTelemetry {

  private Integer meanSpeed;
  private Integer maxSpeed;
  private Integer totalDistance;

  public Integer getMeanSpeed() {
    return meanSpeed;
  }

  public void setMeanSpeed(Integer meanSpeed) {
    this.meanSpeed = meanSpeed;
  }

  public Integer getMaxSpeed() {
    return maxSpeed;
  }

  public void setMaxSpeed(Integer maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  public Integer getTotalDistance() {
    return totalDistance;
  }

  public void setTotalDistance(Integer totalDistance) {
    this.totalDistance = totalDistance;
  }
}
