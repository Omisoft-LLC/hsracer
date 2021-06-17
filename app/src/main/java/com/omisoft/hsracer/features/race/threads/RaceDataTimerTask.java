package com.omisoft.hsracer.features.race.threads;

import android.os.Message;
import android.os.SystemClock;
import com.omisoft.hsracer.common.events.RaceDataEvent;
import com.omisoft.hsracer.features.race.events.RaceWebSocketCommandEvent;
import com.omisoft.hsracer.features.race.structures.RaceDataDTO;
import com.omisoft.hsracer.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.greenrobot.eventbus.EventBus;

/**
 * This timer tasks delivers race data info every second
 * Created by dido on 11.08.17.
 */

public class RaceDataTimerTask implements Runnable {

  public static final String TAG = RaceDataTimerTask.class.getSimpleName();

  @Getter
  @Setter
  private volatile boolean running;

  public RaceDataTimerTask() {
    Utils.getBaseApp().getRaceDataManager().setRaceStartTime(SystemClock.elapsedRealtime());
  }
  @Override
  public void run() {
    running = true;
    while (running) {
      RaceDataDTO raceDataDTO = Utils.getBaseApp().getRaceDataManager().getCurrentRaceData();
      if (raceDataDTO != null) {
        Message message = Message.obtain();
        message.what = RaceWebSocketThread.RACE_DATA;
        message.getData().putParcelable(RaceDataDTO.class.getName(), raceDataDTO);
        EventBus.getDefault().post(new RaceWebSocketCommandEvent(message));
        EventBus.getDefault()
            .post(new RaceDataEvent(raceDataDTO, this.getClass().getSimpleName()));
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
