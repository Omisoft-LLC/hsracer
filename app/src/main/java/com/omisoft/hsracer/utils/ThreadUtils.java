package com.omisoft.hsracer.utils;

import java.lang.Thread.State;

/**
 * Generic thread utils
 * Created by Omisoft LLC. on 6/23/17.
 */

public final class ThreadUtils {

  /**
   * Checks whether the thread is running
   */
  public static boolean threadRunning(Thread thread) {
    return thread != null && State.TERMINATED != thread.getState();
  }

  public static void cancel(Thread thread) {
    thread.interrupt();
  }


}
