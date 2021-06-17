package com.omisoft.hsracer.features.race.threads;

import lombok.Getter;

/**
 * Threads that should be monitored must implement this method
 * Created by dido on 04.09.17.
 */

public abstract class LongMonitoredThread implements Runnable {
  @Getter
  protected volatile ThreadState threadState = ThreadState.WAITING;

  /**
   * Thread statuses
   * WAITING = Initial status before thread run method is called
   * RUNNING = Thread is currently in run method
   * FINISHED = thread has finished normally and should not be restarted
   * CRASHED = thread has finished abnormally and should be restarted
   */
  public enum ThreadState {
    WAITING, RUNNING, FINISHED, CRASHED
  }

}
