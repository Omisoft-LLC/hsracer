package com.omisoft.hsracer.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.constants.FirebaseMsgType;
import java.util.Date;

/**
 * Service to implement push notifications using firebase messaging (TODO use XMPP in version 2)
 */
public class FirebaseMsgService extends FirebaseMessagingService {

  private static final String TAG = FirebaseMsgService.class.getName();

  public FirebaseMsgService() {
  }

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    if (remoteMessage.getData().size() > 0 && remoteMessage.getData().containsKey("type")) {
      FirebaseMsgType type = FirebaseMsgType.valueOf(remoteMessage.getData().get("type"));
      switch (type) {
        case YOUTUBE_UPLOAD_SUCCESS:
          String url = remoteMessage.getData().get("url");
          if (url != null) {
            Intent baseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            PendingIntent intent = PendingIntent
                .getActivity(this, 0, baseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            showNotification(getString(R.string.race_video_available) + url,
                getString(R.string.press_watch), intent);
          }
          break;
      }

    }
  }

  private void showNotification(String title, String content, PendingIntent intent) {
    int notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(content).setContentIntent(intent);
    NotificationManager mNotificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify(notificationId, mBuilder.build());

  }
}
