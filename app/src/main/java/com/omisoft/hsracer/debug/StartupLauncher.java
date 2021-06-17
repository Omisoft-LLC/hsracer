package com.omisoft.hsracer.debug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.omisoft.hsracer.features.MainActivity;

/**
 * Startup Reciever
 * Launched only on debug
 * Created by dido on 30.01.18.
 */

public class StartupLauncher extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
      Intent i = new Intent(context, MainActivity.class);
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(i);
    }
  }
}
