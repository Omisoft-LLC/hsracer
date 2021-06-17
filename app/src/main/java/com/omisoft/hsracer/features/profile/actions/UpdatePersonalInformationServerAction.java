package com.omisoft.hsracer.features.profile.actions;

import static com.omisoft.hsracer.constants.URLConstants.AUTHORIZATION_HEADER;
import static com.omisoft.hsracer.constants.URLConstants.REST_URL;
import static com.omisoft.hsracer.constants.URLConstants.UPDATE_PROFILE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.loader.BaseServerAction;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.DataFromServerDTO;
import com.omisoft.hsracer.dto.ProfileDTO;
import com.omisoft.hsracer.exceptions.ServerException;
import com.omisoft.hsracer.features.profile.events.FailedToUpdateProfileEvent;
import com.omisoft.hsracer.features.profile.events.SuccessUpdateProfileEvent;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.EventBus;

/**
 * Update Personal Information
 * Created by Omisoft LLC. on 6/5/17.
 */

public class UpdatePersonalInformationServerAction extends BaseServerAction<DataFromServerDTO> {

  private static final String TAG = UpdatePersonalInformationServerAction.class.getName();
  private final BaseApp baseApp;
  private final ProfileDTO profileDTO;

  public UpdatePersonalInformationServerAction(BaseApp baseApp,
      ProfileDTO profileDTO) {
    super(baseApp, DataFromServerDTO.class);
    this.baseApp = baseApp;
    this.profileDTO = profileDTO;

  }


  @Override
  public void run() {
    ObjectMapper mapper = getMapper();
    RequestBody bodyRequest;
    try {

      bodyRequest = RequestBody.create(Constants.JSON, mapper.writeValueAsString(profileDTO));

      Request request = new Request.Builder().url(REST_URL + UPDATE_PROFILE).put(bodyRequest)
          .addHeader(AUTHORIZATION_HEADER, baseApp.getAuthId()).build();

      loadRawDataFromServer(request);
      EventBus.getDefault().post(new SuccessUpdateProfileEvent(this.getClass().getSimpleName()));
    } catch (ServerException e) {

      EventBus.getDefault()
          .post(new FailedToUpdateProfileEvent(e.getErrorDTO().getDetailedMessage()));

    } catch (JsonProcessingException e) {
      baseApp.getEventBus().post(new ServerException(e));
    }
  }
}