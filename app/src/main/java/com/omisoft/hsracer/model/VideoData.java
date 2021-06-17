package com.omisoft.hsracer.model;

/**
 * Holds video for race
 * Created by dido on 21.04.17.
 */

public class VideoData {

  private String videoURL;

  private String md5;

  private long fileSize;

  public String getVideoURL() {
    return videoURL;
  }

  public void setVideoURL(String videoURL) {
    this.videoURL = videoURL;
  }


  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }
}
