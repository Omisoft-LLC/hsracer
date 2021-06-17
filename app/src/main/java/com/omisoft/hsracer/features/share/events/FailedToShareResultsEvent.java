package com.omisoft.hsracer.features.share.events;

import lombok.Getter;
import lombok.Setter;

/**
 * Failed to share results
 * Created by developer on 26.09.17.
 */

@Getter
@Setter
public class FailedToShareResultsEvent {

  private String message;

  public FailedToShareResultsEvent(String detailedMessage) {
    this.message = detailedMessage;
  }

}
