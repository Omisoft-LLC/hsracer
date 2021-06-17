package com.omisoft.hsracer.utils;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Screen utils
 */
public final class ScreenUtils {

  private ScreenUtils() {
    throw new UnsupportedOperationException("u can't instantiate me...");
  }

  /**
   * Get phone screen width
   */
  public static int getScreenWidth() {
    return Utils.getApp().getResources().getDisplayMetrics().widthPixels;
  }

  /**
   * get screen height
   */
  public static int getScreenHeight() {
    return Utils.getApp().getResources().getDisplayMetrics().heightPixels;
  }

  /**
   * Set full screen
   *
   * @param activity activity
   */
  public static void setFullScreen(@NonNull final Activity activity) {
    activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
  }

  /**
   * Set activity to landscape
   *
   * @param activity activity
   */
  public static void setLandscape(@NonNull final Activity activity) {
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
  }

  /**
   * Set portrait
   *
   * @param activity activity
   */
  public static void setPortrait(@NonNull final Activity activity) {
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  }

  /**
   * Return true if landscape
   */
  public static boolean isLandscape() {
    return Utils.getApp().getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_LANDSCAPE;
  }

  /**
   * Return true if portrait
   */
  public static boolean isPortrait() {
    return Utils.getApp().getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_PORTRAIT;
  }

  /**
   * Get screen rotation
   *
   * @param activity activity
   */
  public static int getScreenRotation(@NonNull final Activity activity) {
    switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
      default:
      case Surface.ROTATION_0:
        return 0;
      case Surface.ROTATION_90:
        return 90;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_270:
        return 270;
    }
  }

  /**
   * Return screenshot
   *
   * @param activity activity
   * @return Bitmap
   */
  public static Bitmap screenShot(@NonNull final Activity activity) {
    return screenShot(activity, false);
  }

  /**
   * Return screenshot
   *
   * @param activity activity
   * @return Bitmap
   */
  public static Bitmap screenShot(@NonNull final Activity activity, boolean isDeleteStatusBar) {
    View decorView = activity.getWindow().getDecorView();
    decorView.setDrawingCacheEnabled(true);
    decorView.buildDrawingCache();
    Bitmap bmp = decorView.getDrawingCache();
    DisplayMetrics dm = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    Bitmap ret;
    if (isDeleteStatusBar) {
      Resources resources = activity.getResources();
      int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
      int statusBarHeight = resources.getDimensionPixelSize(resourceId);
      ret = Bitmap
          .createBitmap(bmp, 0, statusBarHeight, dm.widthPixels, dm.heightPixels - statusBarHeight);
    } else {
      ret = Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels);
    }
    decorView.destroyDrawingCache();
    return ret;
  }

  /**
   * Check if phone is locked
   */
  public static boolean isScreenLock() {
    KeyguardManager km = (KeyguardManager) Utils.getApp()
        .getSystemService(Context.KEYGUARD_SERVICE);
    return km.inKeyguardRestrictedInputMode();
  }

  /**
   * Set sleep duration
   */
  public static void setSleepDuration(final int duration) {
    Settings.System
        .putInt(Utils.getApp().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, duration);
  }

  /**
   * Return sleep duration
   */
  public static int getSleepDuration() {
    try {
      return Settings.System
          .getInt(Utils.getApp().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
    } catch (Settings.SettingNotFoundException e) {
      e.printStackTrace();
      return -123;
    }
  }

  /**
   * Check if it is tablet
   *
   * @return true if tablet screen
   */
  public static boolean isTablet() {
    return (Utils.getApp().getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK)
        >= Configuration.SCREENLAYOUT_SIZE_LARGE;
  }
}