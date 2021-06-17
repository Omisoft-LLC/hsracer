package com.omisoft.hsracer.features.share.receiver;

import static com.omisoft.hsracer.constants.Constants.TIME_REPEATING;
import static com.omisoft.hsracer.constants.Constants.TRIGGER_REPEATING;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.omisoft.hsracer.services.UploadVideoService;

/**
 * Created by developer on 17.11.17.
 */

public class UploadBroadcastReceiver extends BroadcastReceiver {

  static final String ACTION = "android.intent.action.BOOT_COMPLETED";

  private final String TAG = UploadBroadcastReceiver.class.getName();

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(ACTION)) {
      try {
        Intent uploadingService = new Intent(context, UploadVideoService.class);
        PendingIntent pintent = PendingIntent.getService(context, 0, uploadingService, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarm != null) {
          alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, TRIGGER_REPEATING, TIME_REPEATING,
              pintent);
        }
      } catch (Exception e) {
        Log.wtf(TAG, "onServiceConnected: ", e);
      }
    }
  }
}
