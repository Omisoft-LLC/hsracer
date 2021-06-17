package com.omisoft.hsracer.features.share.events;

import com.omisoft.hsracer.common.BaseEvent;
import com.omisoft.hsracer.dto.PublishResultDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by developer on 07.07.17.
 */
@Getter
@Setter
public class ShareEvent extends BaseEvent {

  private PublishResultDTO publishResultDTO;

  public ShareEvent(PublishResultDTO publishResultDTO, String from, String... to) {
    super(from, to);
    this.publishResultDTO = publishResultDTO;
  }
}
