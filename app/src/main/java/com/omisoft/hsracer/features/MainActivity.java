package com.omisoft.hsracer.features;

import static com.omisoft.hsracer.constants.Constants.SHOWED_DISC;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.constants.URLConstants;
import com.omisoft.hsracer.dto.LoginResponseDTO;
import com.omisoft.hsracer.features.home.HomeActivity;
import com.omisoft.hsracer.features.login.LoginActivity;
import com.omisoft.hsracer.features.login.actions.GetProfileServerAction;
import com.omisoft.hsracer.features.login.actions.LoginServerAction;
import com.omisoft.hsracer.features.login.events.LoginEvent;
import com.omisoft.hsracer.features.login.events.LoginFailedEvent;
import com.omisoft.hsracer.utils.EncodeUtils;
import com.yakivmospan.scytale.Store;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseActivity {

  /**
   * Navigate sto {@link LoginActivity} (if the user did not log in before) or right to the {@link
   * HomeActivity}
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getSharedPreferences().getBoolean(SHOWED_DISC, false)) {
      if (!getApp().isConnected()) {
        showWhiteToastMessage(getString(R.string.no_internet_connection));
        startLoginActivity();
      } else if (getSharedPreferences().contains(Constants.EMAIL) && getSharedPreferences()
          .contains(Constants.PASSWORD)) {
        setContentView(R.layout.activity_atemt_to_login);
        attemptAutoLogin();
      } else {
        startLoginActivity();
      }
    } else {
      setContentView(R.layout.activity_main);
      TextView disclaimerTextView = findViewById(R.id.disclaimer_view);
      disclaimerTextView.setText(EncodeUtils.htmlDecode(getString(R.string.legal_disclaimer)));
    }

  }

  /**
   * Accept the terms for using the application
   */
  public void agreeToDisclaimer(View view) {
    SharedPreferences.Editor editor = getSharedPreferences().edit();
    editor.putBoolean(SHOWED_DISC, true);
    editor.apply();

    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    startActivity(intent);
  }

  /**
   * Navigates the user to the web pages where are described the terms about the application
   */
  public void viewTerms(View view) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLConstants.TERMS_PAGE));
    startActivity(browserIntent);
  }

  private void attemptAutoLogin() {
    Bundle bundle = new Bundle();
    Store store = new Store(getApplicationContext());
    bundle.putString(Constants.PASSWORD,
        getApp().getCrypto().decrypt(getSharedPreferences().getString(Constants.PASSWORD, ""),
            store.getAsymmetricKey(BuildConfig.APPLICATION_ID, null)));
    bundle.putString(Constants.EMAIL, getSharedPreferences().getString(Constants.EMAIL, ""));
    bundle.putString(Constants.FIREBASE_TOKEN,
        getSharedPreferences().getString(Constants.FIREBASE_TOKEN, ""));
    getExecutor().submit(new LoginServerAction(getApp(), bundle));

  }

  /**
   * Sends request to the server to take the info for the logged user
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
   * If the login fails this method is called and the user is navigated to {@link LoginActivity} so
   * he can sign in again
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void loginFailedEventHandler(LoginFailedEvent event) {
    showWhiteToastMessage(event.getErrorMessage());
    startLoginActivity();
  }

  /**
   * If the login fails this method is called and the user is navigated to {@link LoginActivity} so
   * he can sign in again
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void loginFailedServerError(ErrorEvent event) {
    showWhiteToastMessage(getString(event.getStringResource()));
    startLoginActivity();
  }

  /**
   * Stores the personal info about the user after the automatic login
   */
  private void storePersonalInfoInSharedPreference(LoginResponseDTO loginData) {
    Store store = new Store(getApplicationContext());
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putLong(Constants.REST_ID, loginData.getRestId());
    editor.putString(Constants.EMAIL, loginData.getEmail());
    editor.putString(Constants.PASSWORD, getApp().getCrypto().encrypt(loginData.getPassword(),
        store.getAsymmetricKey(BuildConfig.APPLICATION_ID, null)));
    editor.putString(Constants.AUTH_ID, loginData.getAuth());
    editor.putString(Constants.NICK_NAME, loginData.getNickname());
    editor.putString(Constants.AES_KEY, loginData.getAesKey());
    editor.apply();
  }

  /**
   * Starts {@link LoginActivity}
   */
  private void startLoginActivity() {
    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    startActivity(intent);
  }

  /**
   * Starts {@link HomeActivity}
   */
  private void startHomeActivity() {
    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
    startActivity(intent);
  }

}
