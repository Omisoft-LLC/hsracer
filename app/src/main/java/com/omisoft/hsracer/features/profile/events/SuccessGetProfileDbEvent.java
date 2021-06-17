package com.omisoft.hsracer.features.profile.events;

import com.omisoft.hsracer.common.BaseEvent;
import com.omisoft.hsracer.model.Profile;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Omisoft LLC. on 6/19/17.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class SuccessGetProfileDbEvent extends BaseEvent {

  private final Profile profile;
  private final List<String> countries;

  public SuccessGetProfileDbEvent(Profile profile, List<String> countries, String from,
      String... to) {
    super(from, to);
    this.profile = profile;
    this.countries = countries;
  }
}
