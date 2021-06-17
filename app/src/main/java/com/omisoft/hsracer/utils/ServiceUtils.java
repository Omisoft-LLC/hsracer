package com.omisoft.hsracer.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service utils
 */
public final class ServiceUtils {

  private ServiceUtils() {
    throw new UnsupportedOperationException(Utils.NO_INIT);
  }

  /**
   * Fey all running services
   */
  public static Set getAllRunningService() {
    ActivityManager activityManager = (ActivityManager) Utils.getApp()
        .getSystemService(Context.ACTIVITY_SERVICE);
    List<RunningServiceInfo> info = activityManager.getRunningServices(0x7FFFFFFF);
    Set<String> names = new HashSet<>();
    if (info == null || info.size() == 0) {
      return null;
    }
    for (RunningServiceInfo aInfo : info) {
      names.add(aInfo.service.getClassName());
    }
    return names;
  }

  /**
   * Start service
   */
  public static void startService(final String className) {
    try {
      startService(Class.forName(className));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Start service
   */
  public static void startService(final Class<?> cls) {
    Intent intent = new Intent(Utils.getApp(), cls);
    Utils.getApp().startService(intent);
  }

  /**
   * Stop service
   */
  public static boolean stopService(final String className) {
    try {
      return stopService(Class.forName(className));
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Stop service
   */
  public static boolean stopService(final Class<?> cls) {
    Intent intent = new Intent(Utils.getApp(), cls);
    return Utils.getApp().stopService(intent);
  }

  /**
   * 绑定服务
   *
   * @param flags <ul> <li>{@link Context#BIND_AUTO_CREATE}</li> <li>{@link
   * Context#BIND_DEBUG_UNBIND}</li> <li>{@link Context#BIND_NOT_FOREGROUND}</li> <li>{@link
   * Context#BIND_ABOVE_CLIENT}</li> <li>{@link Context#BIND_ALLOW_OOM_MANAGEMENT}</li> <li>{@link
   * Context#BIND_WAIVE_PRIORITY}</li> </ul>
   */
  public static void bindService(final String className, final ServiceConnection conn,
      final int flags) {
    try {
      bindService(Class.forName(className), conn, flags);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Bind service
   *
   * @param flags <ul> <li>{@link Context#BIND_AUTO_CREATE}</li> <li>{@link
   * Context#BIND_DEBUG_UNBIND}</li> <li>{@link Context#BIND_NOT_FOREGROUND}</li> <li>{@link
   * Context#BIND_ABOVE_CLIENT}</li> <li>{@link Context#BIND_ALLOW_OOM_MANAGEMENT}</li> <li>{@link
   * Context#BIND_WAIVE_PRIORITY}</li> </ul>
   */
  public static void bindService(final Class<?> cls, final ServiceConnection conn,
      final int flags) {
    Intent intent = new Intent(Utils.getApp(), cls);
    Utils.getApp().bindService(intent, conn, flags);
  }

  /**
   * Unbind service
   */
  public static void unbindService(final ServiceConnection conn) {
    Utils.getApp().unbindService(conn);
  }

  /**
   * Check if service is running
   *
   * @return {@code true}: or <br>{@code false}:
   */
  public static boolean isServiceRunning(final String className) {
    ActivityManager activityManager = (ActivityManager) Utils.getApp()
        .getSystemService(Context.ACTIVITY_SERVICE);
    List<RunningServiceInfo> info = activityManager.getRunningServices(0x7FFFFFFF);
    if (info == null || info.size() == 0) {
      return false;
    }
    for (RunningServiceInfo aInfo : info) {
      if (className.equals(aInfo.service.getClassName())) {
        return true;
      }
    }
    return false;
  }
}