package com.omisoft.hsracer.utils;


import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class used to store images and videos
 * Created by Omisoft LLC. on 5/10/17.
 */

public class SavingMediaFiles {

  public static final int MEDIA_TYPE_IMAGE = 1;
  public static final int MEDIA_TYPE_VIDEO = 2;
  private static final String TAG = SavingMediaFiles.class.getName();

  /**
   * Create a file Uri for saving an image or video
   */
  public static Uri getOutputMediaFileUri(int type) {
    return Uri.fromFile(getOutputMediaFile(type));
  }

  /**
   * Create a File for saving an image or video
   */
  public static File getOutputMediaFile(int type) {
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.
    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DCIM), "HSRacer");
    // This location works best if you want the created images to be shared
    // between applications and persist after your app has been uninstalled.

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        return null;
      }
    }

    // Create a media file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    File mediaFile;
    if (type == MEDIA_TYPE_IMAGE) {
      mediaFile = new File(mediaStorageDir.getPath() + File.separator +
          "IMG_" + timeStamp + ".jpg");
    } else if (type == MEDIA_TYPE_VIDEO) {
      mediaFile = new File(mediaStorageDir.getPath() + File.separator +
          "HSRACER_VID_" + timeStamp + ".mp4");
    } else {
      return null;
    }

    return mediaFile;
  }

  /**
   * Get video path
   */
  public static String getVideoFilePath(Context context) {
    return Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
        + "HSRacer" + DateFormat.format("yyyy-MM-dd_HH-mm-ss", new Date().getTime()) + ".mp4";
  }

}
