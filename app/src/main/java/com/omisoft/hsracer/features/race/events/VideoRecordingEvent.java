package com.omisoft.hsracer.features.race.events;

import android.os.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Event sent from  VideoRecording thread to UI
 * Created by dido on 15.06.17.
 */
@Getter
@Setter
@AllArgsConstructor
public class VideoRecordingEvent {

  private final Message message;


}
