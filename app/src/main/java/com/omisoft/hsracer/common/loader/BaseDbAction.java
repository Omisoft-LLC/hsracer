package com.omisoft.hsracer.common.loader;

/**
 * Generic db action
 * Created by dido on 07.06.17.
 */

public abstract class BaseDbAction implements Runnable {

  protected String TAG;

  public BaseDbAction() {
    TAG = this.getClass().getSimpleName();

  }


}
