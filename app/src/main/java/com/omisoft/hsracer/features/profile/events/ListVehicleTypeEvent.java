package com.omisoft.hsracer.features.profile.events;

import com.omisoft.hsracer.common.BaseEvent;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by developer on 04.12.17.
 */
@AllArgsConstructor

public class ListVehicleTypeEvent extends BaseEvent {

  @Getter
  private  List<String> vehicles;

  public ListVehicleTypeEvent(List<String> vehicles, String from, String... to) {
    super(from, to);
    this.vehicles = vehicles;

  }

}
