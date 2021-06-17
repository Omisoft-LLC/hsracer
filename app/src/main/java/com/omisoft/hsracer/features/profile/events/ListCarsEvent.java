package com.omisoft.hsracer.features.profile.events;

import com.omisoft.hsracer.common.BaseEvent;
import com.omisoft.hsracer.model.Car;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * List cars event
 * Created by dido on 13.06.17.
 */
@Getter
@Setter
@AllArgsConstructor

public class ListCarsEvent extends BaseEvent {

  private final List<Car> cars;

  public ListCarsEvent(List<Car> cars, String from, String... to) {
    super(from, to);
    this.cars = cars;
  }
}
