package com.omisoft.hsracer.features.profile.actions;

import static com.omisoft.hsracer.constants.URLConstants.ADD_CAR;
import static com.omisoft.hsracer.constants.URLConstants.AUTHORIZATION_HEADER;
import static com.omisoft.hsracer.constants.URLConstants.REST_URL;

import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.common.loader.BaseServerAction;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.CarDTO;
import com.omisoft.hsracer.exceptions.ServerException;
import com.omisoft.hsracer.features.profile.events.FailedToCreateCarEvent;
import com.omisoft.hsracer.features.profile.events.SuccessCreateCarEvent;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.EventBus;

/**
 * Create car on server
 * Created by Omisoft LLC. on 6/5/17.
 */

public class CreateCarServerAction extends BaseServerAction<CarDTO> implements Runnable {

  private static final String TAG = CreateCarServerAction.class.getName();
  private final BaseApp baseApp;
  private final CarDTO newCarDTO;
  private final ObjectMapper mapper;

  public CreateCarServerAction(BaseApp baseApp, CarDTO newCarDTO) {
    super(baseApp, CarDTO.class);
    this.newCarDTO = newCarDTO;
    this.baseApp = baseApp;
    mapper = baseApp.getObjectMapper();
  }

  @Override
  public void run() {
    RequestBody bodyRequest = null;
    try {
      bodyRequest = RequestBody.create(Constants.JSON, mapper.writeValueAsString(newCarDTO));

      Request request = new Request.Builder().url(REST_URL + ADD_CAR).post(bodyRequest)
          .addHeader(AUTHORIZATION_HEADER, baseApp.getAuthId()).build();

      CarDTO carDTO = loadDataFromServer(request);
      Log.wtf(TAG, "run: " + carDTO.toString() );
      EventBus.getDefault()
          .post(new SuccessCreateCarEvent(carDTO.getRestId(),this.getClass().getSimpleName()));
    } catch (ServerException e) {
      EventBus.getDefault().post(new FailedToCreateCarEvent(e.getErrorDTO().getDetailedMessage()));
    } catch (JsonProcessingException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));
    }


  }


}
