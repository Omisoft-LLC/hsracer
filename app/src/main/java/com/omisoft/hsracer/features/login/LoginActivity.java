package com.omisoft.hsracer.features.login;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.LoginResponseDTO;
import com.omisoft.hsracer.features.home.HomeActivity;
import com.omisoft.hsracer.features.login.actions.GetProfileServerAction;
import com.omisoft.hsracer.features.login.actions.LoginServerAction;
import com.omisoft.hsracer.features.login.events.LoginEvent;
import com.omisoft.hsracer.features.login.events.LoginFailedEvent;
import com.omisoft.hsracer.features.register.RegisterActivity;
import com.omisoft.hsracer.services.RaceService;
import com.omisoft.hsracer.utils.AndroidUtils;
import com.yakivmospan.scytale.Store;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoginActivity extends BaseActivity {

  private static final String DEBUG_EMAIL = "dev3@abv.bg";
  private static final String DEBUG_PASSWORD = "asd";
  @BindView(R.id.email)
  EditText emailField;
  @BindView(R.id.password)
  EditText passwordField;
  @BindView(R.id.login_btn)
  Button loginButton;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    setUpDebugLogin();
    disableBluetoothReceiver();
  }

  /**
   * Login button listener
   */
  public void loginOnClickListener(View view) {
    if (!getApp().isConnected()) {
      showWhiteToastMessage(getResources().getString(R.string.no_internet_connection));
      return;
    }
    String email = emailField.getText().toString();
    String password = passwordField.getText().toString();

    if (TextUtils.isEmpty(email)) {
      showWhiteToastMessage(getResources().getString(R.string.empty_field_email));
    } else if (TextUtils.isEmpty(password)) {
      showWhiteToastMessage(getResources().getString(R.string.empty_field_password));
    } else if (AndroidUtils.isEmailValid(email)) {
      showWhiteToastMessage(getResources().getString(R.string.not_correct_email));
    } else {
      Bundle bundle = new Bundle();
      bundle.putString(Constants.PASSWORD, password);
      bundle.putString(Constants.EMAIL, email);
      bundle.putString(Constants.FIREBASE_TOKEN,
          getSharedPreferences().getString(Constants.FIREBASE_TOKEN, ""));
      getExecutor().submit(new LoginServerAction(getApp(), bundle));
    }
  }

  /**
   * Register button listener
   */
  public void registerOnClickListener(View view) {
    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
    startActivity(intent);
  }

  private void setUpDebugLogin() {
    if (BuildConfig.DEBUG) {
      emailField.setText(DEBUG_EMAIL);
      passwordField.setText(DEBUG_PASSWORD);
    }
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

  @Override
  public void onBackPressed() {
    if (RaceService.isSERVICE_IS_STARTED()) {
      stopService(new Intent(LoginActivity.this, RaceService.class));
    }
    finish();
  }

  @Override
  protected void onDestroy() {
    if (RaceService.isSERVICE_IS_STARTED()) {
      stopService(new Intent(LoginActivity.this, RaceService.class));
    }
    super.onDestroy();
  }

  /**
   * If the login is success this method is called from {@link LoginServerAction}
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void loginEventHandler(LoginEvent event) {
    storePersonalInfoInSharedPreference(event.getLoginResponseDTO());
    getExecutor().submit(new GetProfileServerAction(getApp()));
    if (!BuildConfig.DEBUG) {
      Bundle firebaseParamBundle = new Bundle();
      firebaseParamBundle.putLong("REST_ID", event.getLoginResponseDTO().getRestId());
      mFirebaseAnalytics.logEvent(Event.LOGIN, firebaseParamBundle);
    }
    startHomeActivity();
  }

  /**
   * The method is called when login action is not a success due to un correct password or email.
   * Send by {@link LoginServerAction}
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void loginFailedEventHandler(LoginFailedEvent event) {
    showWhiteToastMessage(event.getErrorMessage());
  }

  /**
   * The method is called when login action is not a success due to server problems
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void loginFailedServerError(ErrorEvent event) {
    showWhiteToastMessage(getString(event.getStringResource()));
  }


  private void storePersonalInfoInSharedPreference(LoginResponseDTO loginData) {
    Store store = new Store(getApplicationContext());
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putLong(Constants.REST_ID, loginData.getRestId());
    editor.putString(Constants.EMAIL, loginData.getEmail());
    editor.putString(Constants.PASSWORD, getApp().getCrypto().encrypt(loginData.getPassword(),
        store.getAsymmetricKey(BuildConfig.APPLICATION_ID, null)));
    editor.putString(Constants.API_KEY, loginData.getApiKey());
    editor.putString(Constants.AUTH_ID, loginData.getAuth());
    editor.apply();
  }


  private void startHomeActivity() {
    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
    startActivity(intent);
  }
}