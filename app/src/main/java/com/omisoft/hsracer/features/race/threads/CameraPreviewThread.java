//package com.omisoft.hsracer.features.race.threads;
//
//import android.content.Context;
//import android.content.Intent;
//import android.hardware.Camera;
//import android.media.CamcorderProfile;
//import android.media.MediaRecorder;
//import android.net.Uri;
//import android.os.Build.VERSION;
//import android.os.Build.VERSION_CODES;
//import android.os.Environment;
//import android.os.Message;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import com.omisoft.hsracer.R;
//import com.omisoft.hsracer.common.BaseApp;
//import com.omisoft.hsracer.constants.Constants;
//import com.omisoft.hsracer.utils.Utils;
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.concurrent.LinkedBlockingQueue;
//import lombok.Getter;
//
///**
// * Camera Preview Thread
// * Created by developer on 10.10.17.
// */
//
//public class CameraPreviewThread implements Runnable {
//
//  public static final int INIT_FIRST_TIME_CAMERA = 1;
//  public static final int TURN_ON_CAMERA = 2;
//  public static final int INIT_AND_TURN_ON_CAMERA_AND_MEDIA = 3;
//  public static final int RELEASE_CAMERA_AND_MEDIA = 4;
//  public static final int TURN_OFF_THREAD = 5;
//
//  private Camera mCamera;
//  private MediaRecorder mMediaRecorder;
//  private SurfaceView mSurfaceView;
//  private String dateForVideo;
//  private String folderForVideo;
//  private Context mContext;
//  @Getter
//  private LinkedBlockingQueue<Message> commandQueue;
//  @Getter
//  private File dir;
//
//  private boolean threadIsTurnedOn = true;
//
//  public CameraPreviewThread(Context context, SurfaceView surfaceView, long raceID) {
//
//    this.mContext = context;
//    this.mSurfaceView = surfaceView;
//    commandQueue = new LinkedBlockingQueue<>();
//    dir = new File(
//        Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
//            + context.getString(R.string.app_name) + File.separator
//            + raceID);
//    folderForVideo = dir.getPath();
//    dir.mkdirs();
//  }
//
//  /**
//   * This thread controls the camera.
//   */
//  @Override
//  public void run() {
//    try {
//
//      while (threadIsTurnedOn) {
//        Message message;
//        message = commandQueue.take();
//        switch (message.what) {
//          case INIT_FIRST_TIME_CAMERA:
//            initCameraAndMedia();
//            break;
//          case TURN_ON_CAMERA:
//            startMediaRecorderPreview();
//            break;
//          case INIT_AND_TURN_ON_CAMERA_AND_MEDIA:
//            initCameraAndMedia();
//            Runnable delayTask = new Runnable() {
//              @Override
//              public void run() {
//                startMediaRecorderPreview();
//              }
//            };
//            mSurfaceView.postDelayed(delayTask, 1500);
//            break;
//          case RELEASE_CAMERA_AND_MEDIA:
//            releaseMediaRecorder();
//            releaseCameraAndPreview();
//            break;
//          case TURN_OFF_THREAD:
//            Log.wtf("I WAS STOPPED", "STOP");
//            releaseMediaRecorder();
//            releaseCameraAndPreview();
//            threadIsTurnedOn = false;
//            break;
//        }
//      }
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//
//  }
//
//  /**
//   * Initializing the camera and media recorder
//   */
//  private void initCameraAndMedia() {
//    try {
//      if (mCamera == null) {
//        mCamera = Camera.open();
//        Camera.Parameters param = mCamera.getParameters();
//        param.set("cam_mode", 1);
//        mCamera.setParameters(param);
//        SurfaceHolder holder = mSurfaceView.getHolder();
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        mCamera.setPreviewDisplay(holder);
//        mCamera.unlock();
//        if (mMediaRecorder == null) {
//          dateForVideo = getCurrentTime();
//          mMediaRecorder = new MediaRecorder();
//          mMediaRecorder.setCamera(mCamera);
//          mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
//          mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//          mMediaRecorder.setProfile(CamcorderProfile.get(getVideoQuality()));
//          mMediaRecorder.setOutputFile(getPath());
//          mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
//        }
//
//      }
//    } catch (RuntimeException e) {
//      Log.wtf("CAMERA", "CameraFragment: ", e);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
//
//  /**
//   * Gets the video from folder HSRacer by a given date.
//   */
//  public String getPath() {
//    return folderForVideo + File.separator + dateForVideo + ".mp4";
//  }
//
//  /**
//   * Takes the current time so it can be used like a name for each file
//   */
//  public String getCurrentTime() {
//    Date date = Calendar.getInstance().getTime();
//    SimpleDateFormat format = new SimpleDateFormat("MM_dd_yyyy_hh_mm_ss");
//    return format.format(date);
//  }
//
//  /**
//   * Start the media recorder
//   */
//  private void startMediaRecorderPreview() {
//    Log.wtf("CAMERA", "startMediaRecorderPreview: ");
//    try {
//      mMediaRecorder.prepare();
//      mMediaRecorder.start();
//    } catch (IOException e) {
//      Log.wtf("CAMERA", "startMediaRecorderPreview: ", e);
//    } catch (IllegalStateException e) {
//      Log.wtf("CAMERA", "COULD NOT START VIDEO RECORDING ", e);
//    } catch (NullPointerException e) {
//      Log.wtf("CAMERA", "COULD NOT START VIDEO RECORDING NULL POINTER", e);
//    } catch (RuntimeException e) {
//      Log.wtf("CAMERA", "RUNTIME EXCEPTION ERROR", e);
//    }
//  }
//
//  /**
//   * Releases camera
//   */
//  public void releaseCameraAndPreview() {
//    if (mCamera != null) {
//      mCamera.stopPreview();
//      mCamera.setPreviewCallback(null);
//      mCamera.release();
//      mCamera = null;
//    }
//  }
//
//  /**
//   * Releases media recorder and sends a broadcast to the video gallery. The broadcast is used to
//   * notify the device that there eis a new video
//   */
//  private void releaseMediaRecorder() {
//    if (mCamera != null) {
//      try {
//        mMediaRecorder.stop();
//        mMediaRecorder.reset();
//        mMediaRecorder.release();
//        mMediaRecorder = null;
//        mContext.sendBroadcast(
//            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(getPath()))));
//      } catch (IllegalStateException e) {
//        File emptyFile = new File(getPath());
//        if (emptyFile.exists() && emptyFile.length() == 0) {
//          emptyFile.delete();
//        }
//        mMediaRecorder.release();
//        mMediaRecorder = null;
//      } catch (NullPointerException e) {
//        Log.wtf("CAMERA", "ERROR BECAUSE MEDIA RECORDING IS NULL", e);
//      }
//    }
//
//  }
//
//
//  /**
//   * Gets the video quality from the options located in the drawer->settings of the application
//   */
//  private int getVideoQuality() {
//    BaseApp context = Utils.getBaseApp();
//    String quality = context.getSharedPreferences().getString(Constants.RESOLUTION_KEY, "");
//    if (quality.equalsIgnoreCase("4k")) {
//      if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
//        return CamcorderProfile.QUALITY_2160P;
//      } else {
//        return CamcorderProfile.QUALITY_1080P;
//      }
//    } else if (quality.equalsIgnoreCase("1080p")) {
//      return CamcorderProfile.QUALITY_1080P;
//    } else if (quality.equalsIgnoreCase("720p")) {
//      return CamcorderProfile.QUALITY_720P;
//    } else if (quality.equalsIgnoreCase("480p")) {
//      return CamcorderProfile.QUALITY_480P;
//    } else if (quality.equalsIgnoreCase("144p")) {
//      return CamcorderProfile.QUALITY_LOW;
//    } else {
//      return CamcorderProfile.QUALITY_HIGH;
//    }
//  }
//}
