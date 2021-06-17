package com.omisoft.hsracer.features.profile.actions;

import static com.omisoft.hsracer.constants.URLConstants.AUTHORIZATION_HEADER;
import static com.omisoft.hsracer.constants.URLConstants.REST_URL;
import static com.omisoft.hsracer.constants.URLConstants.UPDATE_CAR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.loader.BaseServerAction;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.CarDTO;
import com.omisoft.hsracer.dto.DataFromServerDTO;
import com.omisoft.hsracer.exceptions.ServerException;
import com.omisoft.hsracer.features.profile.events.FailedToUpdateCarEvent;
import com.omisoft.hsracer.features.profile.events.SuccessUpdateCarEvent;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.EventBus;

/**
 * Update car Action
 * Created by Omisoft LLC. on 6/6/17.
 */

public class UpdateCarServerAction extends BaseServerAction<DataFromServerDTO> implements Runnable {


  private static final String TAG = UpdateCarServerAction.class.getName();
  private final BaseApp baseApp;
  private final CarDTO carDTO;

  public UpdateCarServerAction(BaseApp baseApp, CarDTO car) {
    super(baseApp, DataFromServerDTO.class);
    this.baseApp = baseApp;
    this.carDTO = car;

  }

  @Override
  public void run() {

    RequestBody bodyRequest;
    try {
      bodyRequest = RequestBody.create(Constants.JSON, getMapper().writeValueAsString(carDTO));

      Request request = new Request.Builder().url(REST_URL + UPDATE_CAR + carDTO.getRestId())
          .put(bodyRequest).addHeader(AUTHORIZATION_HEADER, baseApp.getAuthId()).build();

      DataFromServerDTO data = loadRawDataFromServer(request);
      if (data != null && data.getResponseCode() == 200) {
        EventBus.getDefault().post(new SuccessUpdateCarEvent(this.getClass().getSimpleName()));
      }
    } catch (ServerException e) {
      EventBus.getDefault().post(new FailedToUpdateCarEvent(e.getErrorDTO().getDetailedMessage()));
    } catch (JsonProcessingException e) {
      baseApp.getEventBus().post(new ServerException(e));
    }
  }
}
