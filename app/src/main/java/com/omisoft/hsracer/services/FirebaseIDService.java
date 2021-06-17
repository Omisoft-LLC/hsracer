package com.omisoft.hsracer.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.omisoft.hsracer.constants.Constants;

/**
 * Called on app install
 */
public class FirebaseIDService extends FirebaseInstanceIdService {

  private static final String TAG = FirebaseIDService.class.getName();

  public FirebaseIDService() {
  }


  @Override
  public void onTokenRefresh() {
    // Get updated InstanceID token.
    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    editor.putString(Constants.FIREBASE_TOKEN, refreshedToken);
    editor.apply();
  }

}
