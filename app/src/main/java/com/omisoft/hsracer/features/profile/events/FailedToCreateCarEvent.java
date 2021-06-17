package com.omisoft.hsracer.features.profile.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Event - issued on FailedToCreate car
 * Created by developer on 28.08.17.
 */
@Getter
@Setter
@AllArgsConstructor

public class FailedToCreateCarEvent {

  private final String message;

}
