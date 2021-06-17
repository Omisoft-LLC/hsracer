package com.omisoft.hsracer.features.register;

import static com.omisoft.hsracer.utils.AndroidUtils.isEmailValid;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.constants.FirebaseEvent;
import com.omisoft.hsracer.dto.RegistrationDTO;
import com.omisoft.hsracer.features.login.LoginActivity;
import com.omisoft.hsracer.features.profile.actions.CreateProfileServerAction;
import com.omisoft.hsracer.features.profile.events.FailedRegisterEvent;
import com.omisoft.hsracer.features.profile.events.SuccessRegisterEvent;
import com.omisoft.hsracer.utils.AndroidUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Register activity
 * Created by Omisoft LLC. on 4/21/17.
 */

public class RegisterActivity extends BaseActivity {

  private static final float TOAST_HEIGHT = 0.20f;
  @BindView(R.id.email_register)
  EditText emailEditText;
  @BindView(R.id.password_register)
  EditText passwordEditText;
  @BindView(R.id.first_name)
  EditText firstNameEditText;
  @BindView(R.id.last_name)
  EditText lastNameEditText;
  @BindView(R.id.repeat_password_register)
  EditText rePasswordEditText;
  @BindView(R.id.nickname)
  EditText nicknameEditText;
  @BindView(R.id.register)
  Button registerButton;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    disableBluetoothReceiver();
  }

  /**
   * Register button's listener
   */
  public void registerOnClickListener(View view) {
    if (getApp().isConnected()) {
      String email = emailEditText.getText().toString();
      String password = passwordEditText.getText().toString();
      String firstName = firstNameEditText.getText().toString();
      String lastName = lastNameEditText.getText().toString();
      String repeatPassword = rePasswordEditText.getText().toString();
      String nickname = nicknameEditText.getText().toString();
      if (TextUtils.isEmpty(nickname)) {
        showWhiteToastMessage(R.string.empty_field_nickname);
      } else if (isEmailValid(email)) {
        showWhiteToastMessage(R.string.not_correct_email);
      } else if (TextUtils.isEmpty(password)) {
        showWhiteToastMessage(R.string.empty_field_password);
      } else if (TextUtils.isEmpty(repeatPassword)) {
        showWhiteToastMessage(R.string.empty_field_repeat_password);
      } else if (!password.equals(repeatPassword)) {
        showWhiteToastMessage(R.string.password_fields_does_not_match);
      } else {
        RegistrationDTO registrationDTO = new RegistrationDTO(firstName, lastName, email, password,
            nickname);
        getExecutor().submit(new CreateProfileServerAction(getApp(), registrationDTO));
      }
    } else {
      showWhiteToastMessage(R.string.no_internet_connection);
    }
  }

  /**
   *
   * @param event
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void successRegisterHandler(SuccessRegisterEvent event) {
    if (!com.omisoft.hsracer.BuildConfig.DEBUG) {
      Bundle firebaseParamBundle = new Bundle();
      mFirebaseAnalytics.logEvent(FirebaseEvent.REGISTER, firebaseParamBundle);
    }
    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

    startActivity(intent);
  }

  /**
   * Called when the registration is not success from the server. Send by {@link CreateProfileServerAction}
   * @param event
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void failedRegisterHandler(FailedRegisterEvent event) {
    showWhiteToastMessage(event.getMessageError());
  }

  /**
   * Method to disable bluetooth receiver, because it's not needed in this activity
   */
  private void disableBluetoothReceiver() {
    broadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
          final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
              BluetoothAdapter.ERROR);
          switch (state) {
            case BluetoothAdapter.STATE_OFF:
              break;
            case BluetoothAdapter.STATE_TURNING_OFF:
              break;
            case BluetoothAdapter.STATE_ON:
              if (AndroidUtils.isOBDReadReady(getSharedPreferences())) {
              }
              break;
            case BluetoothAdapter.STATE_TURNING_ON:
              break;
          }
        }
      }
    };
  }
}
