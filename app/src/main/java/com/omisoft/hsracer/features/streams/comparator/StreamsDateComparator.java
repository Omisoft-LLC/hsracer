package com.omisoft.hsracer.features.streams.comparator;

import com.omisoft.hsracer.dto.StreamListDTO;
import java.util.Comparator;

/**
 * Streams date comparator
 * Created by developer on 11.09.17.
 */

public class StreamsDateComparator implements Comparator<StreamListDTO> {


  @Override
  public int compare(StreamListDTO t1, StreamListDTO t2) {
    return t2.getStartStreamAt().compareTo(t1.getStartStreamAt());
  }
}
