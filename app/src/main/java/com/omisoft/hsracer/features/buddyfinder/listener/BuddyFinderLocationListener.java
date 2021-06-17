package com.omisoft.hsracer.features.buddyfinder.listener;


import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.omisoft.hsracer.features.buddyfinder.events.BuddyFinderLocationEvent;
import lombok.Getter;
import lombok.Setter;
import org.greenrobot.eventbus.EventBus;

/** From here the buddy finder takes the current location of the user.
 * Created by developer on 13.09.17.
 */
@Getter
@Setter
public class BuddyFinderLocationListener extends LocationCallback {

  @Override
  public void onLocationResult(LocationResult locationResult) {
    if (locationResult.getLastLocation() != null) {
      EventBus.getDefault()
          .post(new BuddyFinderLocationEvent(locationResult.getLastLocation(), this.getClass().getSimpleName()));
    }
  }
}
