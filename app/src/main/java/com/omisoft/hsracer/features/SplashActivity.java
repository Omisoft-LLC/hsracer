package com.omisoft.hsracer.features;


import android.content.Intent;
import android.os.Bundle;
import com.omisoft.hsracer.common.BaseActivity;

public class SplashActivity extends BaseActivity {

  private final static int SPLASH_TIME_OUT = 3000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent i = new Intent(SplashActivity.this, MainActivity.class);
    startActivity(i);
    finish();
  }
}
