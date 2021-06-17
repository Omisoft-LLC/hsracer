package com.omisoft.hsracer.features.profile.events;

import com.omisoft.hsracer.model.Car;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * List all cars
 * Created by dido on 13.06.17.
 */
@Getter
@Setter
@AllArgsConstructor

public class ListCarsArrayListCreateRaceEvent {

  private final List<Car> cars;

}
