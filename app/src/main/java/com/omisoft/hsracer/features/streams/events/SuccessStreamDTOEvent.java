package com.omisoft.hsracer.features.streams.events;

import com.omisoft.hsracer.dto.StreamDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Success event for streams
 * Created by dido on 07.02.18.
 */
@Getter
@AllArgsConstructor
public class SuccessStreamDTOEvent {

  private final List<StreamDTO> streamDTOS;
}
