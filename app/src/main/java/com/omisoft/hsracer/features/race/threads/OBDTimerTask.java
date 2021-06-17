package com.omisoft.hsracer.features.race.threads;

import android.os.Message;
import android.util.Log;
import com.omisoft.hsracer.constants.ObdConstants;
import java.util.concurrent.BlockingQueue;
import lombok.Getter;
import lombok.Setter;

/**
 * Executes obd commands on a timely basis.
 * Created by developer on 04.08.17.
 */

public class OBDTimerTask implements Runnable {

  private static final String TAG = OBDTimerTask.class.getName();
  @Getter
  private BlockingQueue<Message> commandQueue;
  @Setter
  @Getter
  private volatile boolean running;

  public OBDTimerTask(BlockingQueue<Message> commandQueue) {
    this.commandQueue = commandQueue;
  }

  @Override
  public void run() {
    running = true;
    while (running) {
      sendCommandMessageToOBDThread();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

  private void sendCommandMessageToOBDThread() {
    try {
      Message m = Message.obtain();
      m.what = ObdConstants.GET_SPEED_RPM_COOLANT_TEMP;
      commandQueue.put(m);
    } catch (InterruptedException e) {
      Log.wtf(TAG, "Interrupt Exception", e);
    }
  }
}