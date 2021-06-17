package com.omisoft.hsracer.features.buddyfinder.events;

import android.location.Location;
import com.omisoft.hsracer.common.BaseEvent;
import lombok.Getter;
import lombok.Setter;

/** Sends event to BuddyFinder
 * Created by developer on 13.09.17.
 */
@Getter
@Setter
public class BuddyFinderLocationEvent extends BaseEvent {

  private final Location location;

  public BuddyFinderLocationEvent(Location location, String from) {
    super(from);
    this.location = location;

  }

}
