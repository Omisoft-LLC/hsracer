package com.omisoft.hsracer.common.events;

import android.location.Location;
import com.omisoft.hsracer.common.BaseEvent;
import lombok.Getter;

/**
 * Location event. Holds location data
 * Created by dido on 17.08.17.
 */
@Getter
public class LocationEvent extends BaseEvent {
private final Location location;

  public LocationEvent(Location location, String from) {
    super(from);
    this.location = location;

  }
}
