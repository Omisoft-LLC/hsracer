package com.omisoft.hsracer.features.share.events;

import com.omisoft.hsracer.common.BaseEvent;
import com.omisoft.hsracer.dto.PublishDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by developer on 13.07.17.
 * Event to aid ShareSpeedsAction
 */
@Getter
@Setter
public class SpeedsEvent extends BaseEvent {

  private PublishDTO publishDTO;

  public SpeedsEvent(PublishDTO publishDTO, String from, String to) {
    super(from, to);
    this.publishDTO = publishDTO;
  }
}
