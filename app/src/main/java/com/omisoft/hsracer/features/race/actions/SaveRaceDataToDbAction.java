package com.omisoft.hsracer.features.race.actions;

import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.common.loader.BaseDbAction;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.db.AppDatabase;
import com.omisoft.hsracer.features.race.structures.RaceDataDTO;
import com.omisoft.hsracer.model.RaceDataEntity;
import com.omisoft.hsracer.utils.Utils;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Saves race data
 * Created by dido on 14.08.17.
 */

public class SaveRaceDataToDbAction extends BaseDbAction {

  private static final String TAG = SaveRaceDataToDbAction.class.getName();
  private final BaseApp context;


  public SaveRaceDataToDbAction() {
    this.context = Utils.getBaseApp();
  }

  @Override
  public void run() {
    try {
      AppDatabase db = context.getDB();
      List<RaceDataDTO> raceDataDTOList = context.getRaceDataManager().getCurrentRaceDataList();
      RaceDataEntity[] raceDataEntities = new RaceDataEntity[raceDataDTOList.size()];
      int i = 0;
      for (RaceDataDTO raceDataDTO : raceDataDTOList) {
        RaceDataEntity raceDataEntity = new RaceDataEntity();
        raceDataEntity.setLongitude(raceDataDTO.getLongitude());
        raceDataEntity.setLatitude(raceDataDTO.getLatitude());
        raceDataEntity.setGpsSpeed(raceDataDTO.getGpsSpeed());
        raceDataEntity.setCurrentTime(raceDataDTO.getRaceCurrentTime());
        raceDataEntity.setGpsAcceleration(raceDataDTO.getGpsAcceleration());
        raceDataEntity.setObdRpm(raceDataDTO.getObdRpm());
        raceDataEntity.setObdSpeed(raceDataDTO.getObdSpeed());
        raceDataEntity.setRaceId(context.getSharedPreferences().getLong(Constants.RACE_ID, 0));
        raceDataEntities[i] = raceDataEntity;
        i++;
      }
      db.raceDataDAO().create(raceDataEntities);

    } catch (Exception e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_db, e, this.getClass().getSimpleName()));
      e.printStackTrace();

    } finally {
      context.getRaceDataManager().clear();
    }
  }
}
