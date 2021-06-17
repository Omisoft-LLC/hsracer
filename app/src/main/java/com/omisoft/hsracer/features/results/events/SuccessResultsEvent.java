package com.omisoft.hsracer.features.results.events;

import com.omisoft.hsracer.dto.RaceResultDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dido on 13.06.17.
 */
@Getter
@AllArgsConstructor

public class SuccessResultsEvent {

  private List<RaceResultDTO> results;
  private int lastPage;
  private int currentPage;

}
