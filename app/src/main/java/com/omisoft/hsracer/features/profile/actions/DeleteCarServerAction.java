package com.omisoft.hsracer.features.profile.actions;

import static com.omisoft.hsracer.constants.URLConstants.AUTHORIZATION_HEADER;
import static com.omisoft.hsracer.constants.URLConstants.DELETE_CAR;
import static com.omisoft.hsracer.constants.URLConstants.REST_URL;

import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.loader.BaseServerAction;
import com.omisoft.hsracer.dto.DataFromServerDTO;
import com.omisoft.hsracer.exceptions.ServerException;
import com.omisoft.hsracer.features.profile.events.FailedToDeleteCarEvent;
import com.omisoft.hsracer.features.profile.events.SuccessDeleteCarEvent;
import okhttp3.Request;
import org.greenrobot.eventbus.EventBus;

/**
 * Deletes car action
 * Created by Omisoft LLC. on 6/6/17.
 */

public class DeleteCarServerAction extends BaseServerAction<DataFromServerDTO> implements Runnable {

  private Long carId;
  private BaseApp baseApp;
  private int carsActivityAdapterPosition;

  public DeleteCarServerAction(BaseApp baseApp, Long carId, int position) {
    super(baseApp, DataFromServerDTO.class);
    this.baseApp = baseApp;
    this.carId = carId;
    this.carsActivityAdapterPosition = position;

  }


  @Override
  public void run() {
    Request request = new Request.Builder().url(REST_URL + DELETE_CAR + carId)
        .delete()
        .addHeader(AUTHORIZATION_HEADER, baseApp.getAuthId()).build();
    try {
      DataFromServerDTO data = loadRawDataFromServer(request);
      if (data != null && data.getResponseCode() == 200) {
        EventBus.getDefault().post(new SuccessDeleteCarEvent(carsActivityAdapterPosition, this.getClass().getSimpleName()));

      }
    } catch (ServerException e) {
      EventBus.getDefault().post(new FailedToDeleteCarEvent(e.getErrorDTO().getDetailedMessage()));
    }
  }
}