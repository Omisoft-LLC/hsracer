package com.omisoft.hsracer.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import java.util.List;

/**
 * Various activity utils
 */
public final class ActivityUtils {

  private ActivityUtils() {
    throw new UnsupportedOperationException("Can't init class");
  }

  /**
   * Check if activity exists
   *
   * @return {@code true}: br>{@code false}:
   */
  public static boolean isActivityExists(@NonNull final String packageName,
      @NonNull final String className) {
    Intent intent = new Intent();
    intent.setClassName(packageName, className);
    return !(Utils.getApp().getPackageManager().resolveActivity(intent, 0) == null ||
        intent.resolveActivity(Utils.getApp().getPackageManager()) == null ||
        Utils.getApp().getPackageManager().queryIntentActivities(intent, 0).size() == 0);
  }

  /**
   * Start Activity
   */
  public static void startActivity(@NonNull final Class<?> cls) {
    Context context = Utils.getApp();
    startActivity(context, null, context.getPackageName(), cls.getName(), null);
  }

  /**
   * Start Activity
   *
   * @param cls activity
   */
  public static void startActivity(@NonNull final Class<?> cls,
      @NonNull final Bundle options) {
    Context context = Utils.getApp();
    startActivity(context, null, context.getPackageName(), cls.getName(), options);
  }

  /**
   * Start Activity
   *
   * @param activity activity
   * @param cls activity
   */
  public static void startActivity(@NonNull final Activity activity,
      @NonNull final Class<?> cls) {
    startActivity(activity, null, activity.getPackageName(), cls.getName(), null);
  }

  /**
   * Start Activity
   *
   * @param activity activity
   * @param cls activity类
   * @param options 跳转动画
   */
  public static void startActivity(@NonNull final Activity activity,
      @NonNull final Class<?> cls,
      @NonNull final Bundle options) {
    startActivity(activity, null, activity.getPackageName(), cls.getName(), options);
  }

  /**
   * Start Activity with animation
   *
   * @param activity activity
   * @param cls activity
   */
  public static void startActivity(@NonNull final Activity activity,
      @NonNull final Class<?> cls,
      @AnimRes final int enterAnim,
      @AnimRes final int exitAnim) {
    startActivity(activity, null, activity.getPackageName(), cls.getName(), null);
    activity.overridePendingTransition(enterAnim, exitAnim);
  }

  /**
   * Start Activity
   *
   * @param extras extras
   * @param cls activity
   */
  public static void startActivity(@NonNull final Bundle extras,
      @NonNull final Class<?> cls) {
    Context context = Utils.getApp();
    startActivity(context, extras, context.getPackageName(), cls.getName(), null);
  }

  /**
   * Start Activity
   *
   * @param extras extras
   * @param cls activity
   */
  public static void startActivity(@NonNull final Bundle extras,
      @NonNull final Class<?> cls,
      @NonNull final Bundle options) {
    Context context = Utils.getApp();
    startActivity(context, extras, context.getPackageName(), cls.getName(), options);
  }

  /**
   * Start Activity
   *
   * @param extras extras
   * @param activity activity
   * @param cls activity
   */
  public static void startActivity(@NonNull final Bundle extras,
      @NonNull final Activity activity,
      @NonNull final Class<?> cls) {
    startActivity(activity, extras, activity.getPackageName(), cls.getName(), null);
  }

  /**
   * Start Activity
   *
   * @param extras extras
   * @param activity activity
   * @param cls activity
   */
  public static void startActivity(@NonNull final Bundle extras,
      @NonNull final Activity activity,
      @NonNull final Class<?> cls,
      @NonNull final Bundle options) {
    startActivity(activity, extras, activity.getPackageName(), cls.getName(), options);
  }

  /**
   * Start Activity
   *
   * @param extras extras
   * @param activity activity
   * @param cls activity
   */
  public static void startActivity(@NonNull final Bundle extras,
      @NonNull final Activity activity,
      @NonNull final Class<?> cls,
      @AnimRes final int enterAnim,
      @AnimRes final int exitAnim) {
    startActivity(activity, extras, activity.getPackageName(), cls.getName(), null);
    activity.overridePendingTransition(enterAnim, exitAnim);
  }

  /**
   * Start Activity
   */
  public static void startActivity(@NonNull final String pkg,
      @NonNull final String cls) {
    startActivity(Utils.getApp(), null, pkg, cls, null);
  }

  /**
   * Start Activity
   *
   * @param pkg 包名
   * @param cls 全类名
   * @param options 动画
   */
  public static void startActivity(@NonNull final String pkg,
      @NonNull final String cls,
      @NonNull final Bundle options) {
    startActivity(Utils.getApp(), null, pkg, cls, options);
  }

  /**
   * Start Activity
   *
   * @param activity activity
   * @param pkg 包名
   * @param cls 全类名
   */
  public static void startActivity(@NonNull final Activity activity,
      @NonNull final String pkg,
      @NonNull final String cls) {
    startActivity(activity, null, pkg, cls, null);
  }

  /**
   * Start Activity
   *
   * @param activity activity
   */
  public static void startActivity(@NonNull final Activity activity,
      @NonNull final String pkg,
      @NonNull final String cls,
      @NonNull final Bundle options) {
    startActivity(activity, null, pkg, cls, options);
  }

  /**
   * Start Activity
   *
   * @param activity activity
   */
  public static void startActivity(@NonNull final Activity activity,
      @NonNull final String pkg,
      @NonNull final String cls,
      @AnimRes final int enterAnim,
      @AnimRes final int exitAnim) {
    startActivity(activity, null, pkg, cls, null);
    activity.overridePendingTransition(enterAnim, exitAnim);
  }

  /**
   * Start Activity
   *
   * @param extras extras
   */
  public static void startActivity(@NonNull final Bundle extras,
      @NonNull final String pkg,
      @NonNull final String cls) {
    startActivity(Utils.getApp(), extras, pkg, cls, null);
  }

  /**
   * Start Activity
   *
   * @param extras extras
   */
  public static void startActivity(@NonNull final Bundle extras,
      @NonNull final String pkg,
      @NonNull final String cls,
      @NonNull final Bundle options) {
    startActivity(Utils.getApp(), extras, pkg, cls, options);
  }

  /**
   * Start Activity
   *
   * @param activity activity
   * @param extras extras
   */
  public static void startActivity(@NonNull final Bundle extras,
      @NonNull final Activity activity,
      @NonNull final String pkg,
      @NonNull final String cls) {
    startActivity(activity, extras, pkg, cls, null);
  }

  /**
   * Start Activity
   *
   * @param extras extras
   * @param activity activity
   */
  public static void startActivity(@NonNull final Bundle extras,
      @NonNull final Activity activity,
      @NonNull final String pkg,
      @NonNull final String cls,
      @NonNull final Bundle options) {
    startActivity(activity, extras, pkg, cls, options);
  }

  /**
   * Start Activity
   *
   * @param extras extras
   */
  public static void startActivity(@NonNull final Bundle extras,
      @NonNull final Activity activity,
      @NonNull final String pkg,
      @NonNull final String cls,
      @AnimRes final int enterAnim,
      @AnimRes final int exitAnim) {
    startActivity(activity, extras, pkg, cls, null);
    activity.overridePendingTransition(enterAnim, exitAnim);
  }

  @SuppressLint("ObsoleteSdkInt")
  private static void startActivity(final Context context,
      final Bundle extras,
      final String pkg,
      final String cls,
      final Bundle options) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    if (extras != null) {
      intent.putExtras(extras);
    }
    intent.setComponent(new ComponentName(pkg, cls));
    if (options != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      context.startActivity(intent, options);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * launcher activity
   *
   * @return launcher activity
   */
  public static String getLauncherActivity(@NonNull final String packageName) {
    Intent intent = new Intent(Intent.ACTION_MAIN, null);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    PackageManager pm = Utils.getApp().getPackageManager();
    List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
    for (ResolveInfo aInfo : info) {
      if (aInfo.activityInfo.packageName.equals(packageName)) {
        return aInfo.activityInfo.name;
      }
    }
    return "no " + packageName;
  }


  /**
   * /get top activity Activity
   *
   * @return top activity from stack
   */
  public static Activity getTopActivity() {
    if (Utils.sTopActivityWeakRef != null) {
      Activity activity = Utils.sTopActivityWeakRef.get();
      if (activity != null) {
        return activity;
      }
    }
    return Utils.sActivityList.get(Utils.sActivityList.size() - 1);
  }

  /**
   * finish all activities
   */
  public static void finishAllActivities() {
    List<Activity> activityList = Utils.sActivityList;
    for (int i = activityList.size() - 1; i >= 0; --i) {
      activityList.get(i).finish();
      activityList.remove(i);
    }
  }
}