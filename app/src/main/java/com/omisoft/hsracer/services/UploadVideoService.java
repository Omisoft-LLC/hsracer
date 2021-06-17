package com.omisoft.hsracer.services;


import static com.omisoft.hsracer.constants.Constants.CURRENT_VIDEO_FOR_UPLOADING;
import static com.omisoft.hsracer.constants.Constants.NUMBER_OF_VIDEOS;
import static com.omisoft.hsracer.constants.Constants.VIDEOS_FOR_UPLOADING_BUNDLE;
import static com.omisoft.hsracer.constants.Constants.VIDEO_PREFERENCES;
import static com.omisoft.hsracer.features.share.actions.UploadVideoAction.UPLOAD_FAILED;
import static com.omisoft.hsracer.features.share.actions.UploadVideoAction.UPLOAD_FAILED_DUE_TO_INTERNET;
import static com.omisoft.hsracer.features.share.actions.UploadVideoAction.UPLOAD_PENDING;
import static com.omisoft.hsracer.features.share.actions.UploadVideoAction.UPLOAD_SUCCESSFUL;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.features.share.actions.UploadVideoAction;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Service  will upload the videos which the user agreed to upload in the
 * UploadVideoActivity. That uploading will be done only when there is Wi-Fi connection.
 *
 * Created by developer on 08.11.17.
 */

public class UploadVideoService extends Service {

  private static final String TAG = UploadVideoService.class.getName();

  private static Thread videoThread;

  static class LocalHandler extends Handler {

    private Context context;
    private final int ID_NOTIFICATION = 4291;

    public LocalHandler(Context context, int startId) {
      this.context = context;
    }

    /**
     * This handle shows a message in the status bar. The notification depends on the status of the
     * video (uploaded or not). The notifications are coming from {@link UploadVideoAction}
     */
    @Override
    public void handleMessage(Message msg) {
      Bundle bundle = msg.getData();
      switch (msg.what) {
        case UPLOAD_PENDING:
          NotificationManager uploadNotificationPending = (NotificationManager) context
              .getSystemService(Context.NOTIFICATION_SERVICE);
          Builder pendingBuilder = new Notification.Builder(context);
          pendingBuilder.setContentTitle(context.getString(R.string.uploading))
              .setContentText(context.getString(
                  R.string.please_wait_while_uploading))
              .setSubText(context
                  .getString(R.string.number_of_uploading_files,
                      bundle.getInt(NUMBER_OF_VIDEOS)))
              .setSmallIcon(R.drawable.ic_stat_name)
              //TODO set a sound notification here
              .setOngoing(true);
          if (uploadNotificationPending != null) {
            uploadNotificationPending.notify(ID_NOTIFICATION, pendingBuilder.build());
          }

          break;
        case UPLOAD_SUCCESSFUL:

          NotificationManager updatingVideoSuccessful = (NotificationManager) context
              .getSystemService(Service.NOTIFICATION_SERVICE);
          Notification.Builder successBuilder = new Notification.Builder(context);
          successBuilder.setContentTitle(context.getString(R.string.uploading))
              .setContentText(context.getString(R.string.upload_complete))
              .setSubText(context
                  .getString(R.string.current_file_state,
                      bundle.getInt(CURRENT_VIDEO_FOR_UPLOADING),
                      bundle.getInt(NUMBER_OF_VIDEOS)))
              .setSmallIcon(R.drawable.ic_stat_name);
          if (updatingVideoSuccessful != null) {
            updatingVideoSuccessful.notify(ID_NOTIFICATION, successBuilder.build());
          }
          break;

        case UPLOAD_FAILED:
          NotificationManager updatingVideoFailed = (NotificationManager) context
              .getSystemService(Service.NOTIFICATION_SERVICE);
          Notification.Builder failedBuilder = new Notification.Builder(context);
          failedBuilder.setContentTitle(context.getString(R.string.uploading))
              .setContentText(context.getString(R.string.upload_failed))
              .setSubText(context
                  .getString(R.string.current_file_state,
                      bundle.getInt(CURRENT_VIDEO_FOR_UPLOADING),
                      bundle.getInt(NUMBER_OF_VIDEOS)))
              .setSmallIcon(R.drawable.ic_stat_name);
          if (updatingVideoFailed != null) {
            updatingVideoFailed.notify(ID_NOTIFICATION, failedBuilder.build());
          }
          break;
        case UPLOAD_FAILED_DUE_TO_INTERNET:
          NotificationManager updatingVideoFailedConnection = (NotificationManager) context
              .getSystemService(Service.NOTIFICATION_SERVICE);
          Notification.Builder failedConnectionBuilder = new Notification.Builder(context);
          failedConnectionBuilder.setContentTitle(context.getString(R.string.uploading))
              .setContentText(context.getString(R.string.upload_failed))
              .setSubText(context
                  .getString(R.string.current_file_state,
                      bundle.getInt(CURRENT_VIDEO_FOR_UPLOADING),
                      bundle.getInt(NUMBER_OF_VIDEOS)))
              .setSmallIcon(R.drawable.ic_stat_name);
          if (updatingVideoFailedConnection != null) {
            updatingVideoFailedConnection.notify(ID_NOTIFICATION, failedConnectionBuilder.build());
          }
          Handler handler = new Handler();
          handler.post(new Runnable() {
            @Override
            public void run() {
              Toast.makeText(context, "Uploading successful!",
                  Toast.LENGTH_LONG)
                  .show();
            }
          });
          break;
      }
    }
  }

  protected Handler handler;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    Log.wtf(TAG, "TRYING TO BIND");
    return null;
  }


  @Override
  public void onDestroy() {
    Log.wtf(TAG, "UploadVideoService :  DESTROYED");
    if (!videoThread.isAlive()) {
      videoThread.interrupt();
    }
    super.onDestroy();
  }

  /**
   * Starting a sticky service with which are taken the videos marked by the user in {@link
   * com.omisoft.hsracer.features.share.UploadVideoActivity} and the video selected in the gallery
   * nd then chosen to be uploaded by {@link com.omisoft.hsracer.features.share.UploadVideoGalleryActivity}
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    Notification failedBuilder = new Notification();
    startForeground(startId, failedBuilder);
    if (intent != null) {
      Bundle bundle = intent.getBundleExtra(VIDEOS_FOR_UPLOADING_BUNDLE);
      if (bundle != null) {
        putVideosForUploading(bundle);
      }
    }
    if (videoThread != null && (videoThread.getState().equals(State.RUNNABLE) || videoThread
        .getState().equals(State.WAITING) || videoThread.getState().equals(State.BLOCKED))) {
      //TODO: put something here to notify the user when his videos will be uploaded if there are others for uploading first
    } else {
      handler = new LocalHandler(getApplicationContext(), startId);
      UploadVideoAction uploadVideoThread = new UploadVideoAction(getApplicationContext(), handler);
      videoThread = new Thread(uploadVideoThread);
      videoThread.start();
    }
    return START_STICKY;
  }

  /**
   * Takes the videos provided by {@link com.omisoft.hsracer.features.share.UploadVideoActivity} and
   * {@link com.omisoft.hsracer.features.share.UploadVideoGalleryActivity} and puts them in a
   * collection. This collection later is used by the {@link UploadVideoAction} thread.
   */
  private void putVideosForUploading(Bundle bundle) {
    ArrayList<String> arrayListWithPaths = bundle
        .getStringArrayList(Constants.VIDEOS_FOR_UPLOADING);
    if (arrayListWithPaths != null) {
      SharedPreferences sharedPreferences = getApplicationContext()
          .getSharedPreferences(VIDEO_PREFERENCES, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      Set<String> oldList = sharedPreferences
          .getStringSet(Constants.VIDEOS_FOR_UPLOADING, new HashSet<String>());
      oldList.addAll(arrayListWithPaths);
      editor.putStringSet(Constants.VIDEOS_FOR_UPLOADING, oldList);
      if (editor.commit()) {
        Log.d(TAG, "WRITING IN SERVICE");
      }
    }
  }
}