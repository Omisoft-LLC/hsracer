package com.omisoft.hsracer.features.login.actions;

import static com.omisoft.hsracer.constants.URLConstants.LOGIN_EP;
import static com.omisoft.hsracer.constants.URLConstants.REST_URL;

import android.os.Bundle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.common.loader.BaseServerAction;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.DataFromServerDTO;
import com.omisoft.hsracer.dto.LoginDTO;
import com.omisoft.hsracer.dto.LoginResponseDTO;
import com.omisoft.hsracer.exceptions.ServerException;
import com.omisoft.hsracer.features.login.events.LoginEvent;
import com.omisoft.hsracer.features.login.events.LoginFailedEvent;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.EventBus;

/**
 * Login to server action
 * Created by dido on 13.06.17.
 */

public class LoginServerAction extends BaseServerAction<DataFromServerDTO> {

  private static final String TAG = LoginServerAction.class.getName();

  private final Bundle bundle;

  public LoginServerAction(BaseApp context, Bundle data) {
    super(context, DataFromServerDTO.class);
    this.bundle = data;
  }

  @Override
  public void run() {
    LoginDTO userLoginDTO = new LoginDTO();
    userLoginDTO.setEmail(bundle.getString(Constants.EMAIL));
    userLoginDTO.setPassword(bundle.getString(Constants.PASSWORD));
    userLoginDTO.setFireBaseToken(bundle.getString(Constants.FIREBASE_TOKEN));
    Request request;
    try {
      ObjectMapper mapper = getMapper();
      RequestBody bodyRequest = RequestBody
          .create(Constants.JSON, mapper.writeValueAsString(userLoginDTO));
      request = new Request.Builder().url(REST_URL + LOGIN_EP)
          .post(bodyRequest).build();
      DataFromServerDTO data = loadRawDataFromServer(request);
      if (data != null && data.getResponseCode() == 200) {
        LoginResponseDTO login = mapper.readValue(data.getBody(), LoginResponseDTO.class);
        login.setEmail(userLoginDTO.getEmail());
        login.setPassword(userLoginDTO.getPassword());
        login.setAuth(data.getAuthId());
        login.setAesKey(data.getAesKey());

        EventBus.getDefault().post(new LoginEvent(login, this.getClass().getSimpleName()));

      }
    } catch (ServerException e) {
      EventBus.getDefault().post(
          new LoginFailedEvent(this.getClass().getSimpleName(),
              e.getErrorDTO().getDetailedMessage()));
    } catch (IOException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, this.getClass().getSimpleName()));
    }
  }
}