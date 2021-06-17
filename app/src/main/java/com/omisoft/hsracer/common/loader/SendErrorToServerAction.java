package com.omisoft.hsracer.common.loader;

import static com.omisoft.hsracer.constants.URLConstants.REPORT_ERROR;
import static com.omisoft.hsracer.constants.URLConstants.REST_URL;

import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.events.SuccessSendErrorToServerEvent;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.DataFromServerDTO;
import com.omisoft.hsracer.dto.ErrorDTO;
import com.omisoft.hsracer.exceptions.ServerException;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.EventBus;

/**
 * Sends error to server
 * Created by dido on 07.06.17.
 */


public class SendErrorToServerAction extends BaseServerAction<ErrorDTO> implements Runnable {

  private static final String TAG = SendErrorToServerAction.class.getName();
  private final BaseApp baseApp;
  private final ErrorDTO errorDTO;

  public SendErrorToServerAction(BaseApp baseApp, ErrorDTO errorDTO) {
    super(baseApp, ErrorDTO.class);
    this.baseApp = baseApp;
    this.errorDTO = errorDTO;

  }


  @Override
  public void run() {
    RequestBody bodyRequest = null;
    try {
      bodyRequest = RequestBody.create(Constants.JSON, getMapper().writeValueAsString(errorDTO));

      Request request = new Request.Builder().url(REST_URL + REPORT_ERROR).post(bodyRequest)
          .build();
      DataFromServerDTO data = loadRawDataFromServer(request);
      if (data != null) {
        if (data.getResponseCode() == 200) {
          EventBus.getDefault()
              .post(new SuccessSendErrorToServerEvent(this.getClass().getSimpleName()));

        } else {
          EventBus.getDefault().post(new ServerException(data.getResponseCode(), "Error"));
        }
      }
    } catch (ServerException | JsonProcessingException e) {
      Log.wtf(TAG, "Json Exception", e);

      baseApp.getEventBus().post(new ServerException(e));
    }
  }
}