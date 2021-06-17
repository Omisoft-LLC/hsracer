package com.omisoft.hsracer.dto.firebase;

import com.omisoft.hsracer.constants.FirebaseMsgType;
import lombok.Getter;
import lombok.Setter;

/**
 * Notification for youtube
 */
@Getter
@Setter
public class YoutubeInfoDTO {

  private String url;
  private final FirebaseMsgType type = FirebaseMsgType.YOUTUBE_UPLOAD_SUCCESS;

}
