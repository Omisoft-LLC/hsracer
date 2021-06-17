package com.omisoft.hsracer.features.profile.events;

import android.support.annotation.Nullable;
import com.omisoft.hsracer.common.BaseEvent;
import lombok.AllArgsConstructor;

/**
 * Created by developer on 06.12.17.
 */
@AllArgsConstructor

public class SuccessfulDBSaveCarEvent extends BaseEvent{

  public SuccessfulDBSaveCarEvent(String from, @Nullable String... to) {
    super(from, to);
  }
}
