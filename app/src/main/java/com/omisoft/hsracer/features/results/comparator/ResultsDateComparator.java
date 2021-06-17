package com.omisoft.hsracer.features.results.comparator;

import com.omisoft.hsracer.dto.RaceResultDTO;
import java.util.Comparator;

/**
 * Race date comparator
 * Created by developer on 11.09.17.
 */

public class ResultsDateComparator implements Comparator<RaceResultDTO> {


  @Override
  public int compare(RaceResultDTO t1, RaceResultDTO t2) {
    return t2.getStartDate().compareTo(t1.getStartDate());
  }
}
