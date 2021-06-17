package com.omisoft.hsracer.features.profile.events;

import android.support.annotation.Nullable;
import com.omisoft.hsracer.common.BaseEvent;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by developer on 04.12.17.
 */
@AllArgsConstructor

public class ListOfModelsByMakeEvent extends BaseEvent {

  @Getter
  private List<String> models;

  public ListOfModelsByMakeEvent(List<String> models, String from, @Nullable String... to) {
    super(from, to);
    this.models = models;
  }
}
