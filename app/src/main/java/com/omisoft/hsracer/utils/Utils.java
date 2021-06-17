package com.omisoft.hsracer.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.omisoft.hsracer.common.BaseApp;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Generic Android Utils
 */
public final class Utils {

  public static final String NO_INIT = "Initialization is forbidden";
  @SuppressLint("StaticFieldLeak")
  private static Application sApplication;

  static WeakReference<Activity> sTopActivityWeakRef;
  static List<Activity> sActivityList = new LinkedList<>();

  private static Application.ActivityLifecycleCallbacks mCallbacks = new Application.ActivityLifecycleCallbacks() {
    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
      sActivityList.add(activity);
      setTopActivityWeakRef(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
      setTopActivityWeakRef(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
      setTopActivityWeakRef(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
      sActivityList.remove(activity);
      ToastUtils.cancel();
    }
  };

  private Utils() {
    throw new UnsupportedOperationException("init not allowed");
  }

  /**
   * Init utils lib
   */
  public static void init(@NonNull final Application app) {
    Utils.sApplication = app;
    app.registerActivityLifecycleCallbacks(mCallbacks);
  }

  /**
   * Get Application
   *
   * @return Application
   */
  public static Application getApp() {
    if (sApplication != null) {
      return sApplication;
    }
    throw new NullPointerException("u should init first");
  }

  public static BaseApp getBaseApp() {
    if (sApplication != null) {
      return (BaseApp) sApplication;
    }
    throw new NullPointerException("u should init first");
  }

  private static void setTopActivityWeakRef(Activity activity) {
    if (sTopActivityWeakRef == null || !activity.equals(sTopActivityWeakRef.get())) {
      sTopActivityWeakRef = new WeakReference<>(activity);
    }
  }
}