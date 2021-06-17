package com.omisoft.hsracer.features.profile;

import android.util.Log;
import android.webkit.JavascriptInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omisoft.hsracer.dto.statistics.PositionStatDTO;

/**
 * Javascript bridge for stats
 * Created by dido on 28.12.17.
 */

public class JavascriptStatistics {

  private final PositionStatDTO positionStat;
  private final ObjectMapper objectMapper;

  public JavascriptStatistics(PositionStatDTO positionStat, ObjectMapper objectMapper) {
    this.positionStat = positionStat;
    this.objectMapper = objectMapper;
  }

  /**
   * Returns pos statistics
   */
  @JavascriptInterface
  public String getPosStat() {
    try {
      String data = objectMapper.writeValueAsString(positionStat);
      Log.d(JavascriptStatistics.class.getSimpleName(), data);
      return data;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return "";
  }

}
