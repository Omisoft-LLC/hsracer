package com.omisoft.hsracer.common;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.os.Build;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception Handler
 * Created by dido on 07.06.17.
 */

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

  private final BaseApp myContext;
  private static final String LINE_SEPARATOR = "\n";
  private static final String TAG = "ExceptionHandler";

  public ExceptionHandler(BaseApp context) {
    myContext = context;
  }

  @Override
  public void uncaughtException(Thread thread, Throwable exception) {
    StringWriter stackTrace = new StringWriter();
    exception.printStackTrace(new PrintWriter(stackTrace));
    String errorReport = "************ CAUSE OF ERROR ************\n\n" +
        stackTrace.toString() +
        "\n************ DEVICE INFORMATION ***********\n" +
        "Brand: " +
        Build.BRAND +
        LINE_SEPARATOR +
        "Device: " +
        Build.DEVICE +
        LINE_SEPARATOR +
        "Model: " +
        Build.MODEL +
        LINE_SEPARATOR +
        "Id: " +
        Build.ID +
        LINE_SEPARATOR +
        "Product: " +
        Build.PRODUCT +
        LINE_SEPARATOR +
        "\n************ FIRMWARE ************\n" +
        "SDK: " +
        Build.VERSION.SDK_INT +
        LINE_SEPARATOR +
        "Release: " +
        Build.VERSION.RELEASE +
        LINE_SEPARATOR +
        "Incremental: " +
        Build.VERSION.INCREMENTAL +
        LINE_SEPARATOR;

    Intent errorActivity = new Intent("com.omisoft.hsracer.common.ErrorActivity");
    errorActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    errorActivity.putExtra("error", errorReport);

    PendingIntent pendingIntent = PendingIntent.getActivity(myContext, 22, errorActivity, 0);

    try {
      pendingIntent.send();
    } catch (CanceledException e) {
      e.printStackTrace();
    }

    android.os.Process.killProcess(android.os.Process.myPid());
    System.exit(10);
  }

}