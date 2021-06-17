package com.omisoft.hsracer.features.buddyfinder.threads;

import static com.omisoft.hsracer.services.RaceService.START_BUDDY_FINDER_ACTION;
import static com.omisoft.hsracer.services.RaceService.STOP_BUDDY_FINDER_ACTION;
import static com.omisoft.hsracer.utils.ThreadUtils.threadRunning;

import android.os.Message;
import android.util.Log;
import com.omisoft.hsracer.features.buddyfinder.events.BuddyFinderWebSocketCommandEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;
import lombok.Setter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Main BF Thread
 * Created by Omisoft LLC. on 6/23/17.
 */
// TOD Refactor
public class BuddyFinderLifeCycleThread implements Runnable {

  private static final String TAG = BuddyFinderLifeCycleThread.class.getName();
  @Getter
  private final BlockingQueue<Message> commandQueue;
  private Thread bfThread;
  private BuddyFinderWebSocketThread buddyFinderThread;
  @Getter
  @Setter
  private String name;

  public BuddyFinderLifeCycleThread() {
    setName("Buddy Finder Lifecycle Thread");
    commandQueue = new LinkedBlockingQueue<>();
    EventBus.getDefault().register(this);
  }

  @Override
  public void run() {
    while (!Thread.interrupted()) {

      Message message;
      try {
        message = commandQueue.take();

        switch (message.what) {
          case START_BUDDY_FINDER_ACTION:
            startBuddyFinderAction();
            break;
          case STOP_BUDDY_FINDER_ACTION:
            stopBuddyFinderAction();
            break;
        }

      } catch (InterruptedException e) {
        return;
      }
    }
  }

  private void startBuddyFinderAction() {
    if (!threadRunning(bfThread)) {
      buddyFinderThread = new BuddyFinderWebSocketThread();
      bfThread = new Thread(buddyFinderThread);
      bfThread.setPriority(7);
      bfThread.start();
    }
  }

  private void stopBuddyFinderAction() {
    if (threadRunning(bfThread)) {
      bfThread.interrupt();
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void buddyFinderWebSocketCommandEvent(BuddyFinderWebSocketCommandEvent event) {
    Message inputMessage = event.getMessage();
    try {
      buddyFinderThread.getCommandQueue().put(inputMessage);
    } catch (InterruptedException e) {
      Log.wtf(TAG, "Buddy finder exception", e);
    }
  }
}
