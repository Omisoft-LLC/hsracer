package com.omisoft.hsracer.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.events.SuccessSendErrorToServerEvent;
import com.omisoft.hsracer.common.loader.SendErrorToServerAction;
import com.omisoft.hsracer.constants.Severity;
import com.omisoft.hsracer.dto.ErrorDTO;
import com.omisoft.hsracer.features.MainActivity;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Handles uncaught errors. Allows sending errors to developers
 */
public class ErrorActivity extends BaseActivity {

  @BindView(R.id.errorBox)
  TextView error;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_error);
    error.setText(getIntent().getStringExtra("error") + "\n User ID: " + getUserEmail());

  }

  /**
   * Send error to server
   */
  public void sendError(View view) {
    if (getApp().isConnected()) {
      String errorMsg = error.getText().toString();
      ErrorDTO errorDTO = new ErrorDTO("ANR Exception", errorMsg);
      errorDTO.setSeverity(Severity.FATAL);
      SendErrorToServerAction action = new SendErrorToServerAction(getApp(), errorDTO);
      getExecutor().submit(action);
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void successSendErrorHandler(SuccessSendErrorToServerEvent event) {
//    showWhiteToastMessage(getString(R.string.thank_you));
    relaunchApp(null);
  }

  /**
   * Relaunch app
   */
  public void relaunchApp(View view) {
    Intent mStartActivity = new Intent(this, MainActivity.class);

    int mPendingIntentId = 23456;
    PendingIntent mPendingIntent = PendingIntent
        .getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
    AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
    System.exit(0);

  }

}
