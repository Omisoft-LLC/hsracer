package com.omisoft.hsracer.features.share.events;

import com.omisoft.hsracer.common.BaseEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * Event for video uploading, contains status of the event
 * Created by developer on 17.07.17.
 */
@Getter
@Setter
public class UploadVideoEvent extends BaseEvent {

  private final int UPLOAD_STATUS;

  public UploadVideoEvent(int uploadStatus, String from, String... to) {
    super(from, to);
    this.UPLOAD_STATUS = uploadStatus;
  }
}
