package com.omisoft.hsracer.ws.protocol.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by nslavov on 5/16/17.
 */
@Getter
@Setter
@ToString
public class SyncRaceDTO {

  private boolean sync;

  public SyncRaceDTO() {
  }

  public SyncRaceDTO(boolean sync) {
    this.sync = sync;
  }
}
