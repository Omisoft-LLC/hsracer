package com.omisoft.hsracer.features.profile.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Failed to update car
 * Created by developer on 28.08.17.
 */

@Getter
@Setter
@AllArgsConstructor
public class FailedToUpdateCarEvent {

  private final String message;

}
