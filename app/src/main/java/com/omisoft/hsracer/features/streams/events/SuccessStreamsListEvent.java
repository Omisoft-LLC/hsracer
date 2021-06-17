package com.omisoft.hsracer.features.streams.events;

import com.omisoft.hsracer.dto.StreamListDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Success event for streams
 * Created by dido on 13.06.17.
 */
@Getter
@AllArgsConstructor
public class SuccessStreamsListEvent {

  private final List<StreamListDTO> results;
  private final int lastPage;
  private final int currentPage;


}
