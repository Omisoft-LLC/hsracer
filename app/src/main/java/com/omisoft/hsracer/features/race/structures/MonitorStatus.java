package com.omisoft.hsracer.features.race.structures;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Monitor status
 * Created by dido on 04.09.17.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MonitorStatus {

  private  boolean hasNetwork;
  private  boolean hasGPS;
  private  boolean hasOBD;
  private  boolean recording;

  @Override
  public boolean equals(Object o) {

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MonitorStatus that = (MonitorStatus) o;

    if (hasNetwork != that.isHasNetwork()) {
      return false;
    }
    if (hasGPS != that.isHasGPS()) {
      return false;
    }
    if (hasOBD != that.isHasOBD()) {
      return false;
    }
    return recording == that.isRecording();

  }

  @Override
  public int hashCode() {
    int result = (hasNetwork ? 1 : 0);
    result = 31 * result + (hasGPS ? 1 : 0);
    result = 31 * result + (hasOBD ? 1 : 0);
    result = 31 * result + (recording ? 1 : 0);
    return result;
  }


}

