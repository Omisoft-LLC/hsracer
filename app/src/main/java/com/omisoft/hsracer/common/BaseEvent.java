package com.omisoft.hsracer.common;

import android.support.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Super-class for base event
 * Created by dido on 13.06.17.
 */
@Getter
@NoArgsConstructor
public abstract class BaseEvent {

  protected String from;
  @Nullable
  protected String[] to;

  protected BaseEvent(String from, @Nullable String... to) {
    this.from = from;
    this.to = to;
  }

}
