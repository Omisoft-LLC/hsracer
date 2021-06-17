package com.omisoft.hsracer.features.race.events;

import android.os.Message;
import android.view.SurfaceHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Omisoft LLC. on 6/28/17.
 */


@Getter
@Setter
@AllArgsConstructor
public class VideoRecordingCommandEvent {

  private final Message message;
  private final SurfaceHolder surfaceHolder;


}

