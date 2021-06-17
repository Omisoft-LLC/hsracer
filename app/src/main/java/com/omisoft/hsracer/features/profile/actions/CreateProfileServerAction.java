package com.omisoft.hsracer.features.profile.actions;

import static com.omisoft.hsracer.constants.URLConstants.CREATE_EP;
import static com.omisoft.hsracer.constants.URLConstants.REST_URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.loader.BaseServerAction;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.LoginResponseDTO;
import com.omisoft.hsracer.dto.RegistrationDTO;
import com.omisoft.hsracer.dto.SuccessDTO;
import com.omisoft.hsracer.exceptions.ServerException;
import com.omisoft.hsracer.features.profile.events.FailedRegisterEvent;
import com.omisoft.hsracer.features.profile.events.SuccessRegisterEvent;
import com.omisoft.hsracer.model.Profile;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.EventBus;

/**
 * Create profile on server
 * Created by dido on 09.06.17.
 */

public class CreateProfileServerAction extends BaseServerAction<SuccessDTO> implements Runnable {

  private static final String TAG = CreateProfileServerAction.class.getName();
  private final BaseApp baseApp;
  private final RegistrationDTO registrationDTO;

  public CreateProfileServerAction(BaseApp baseApp, RegistrationDTO registrationDTO) {
    super(baseApp, SuccessDTO.class);

    this.registrationDTO = registrationDTO;
    this.baseApp = baseApp;
  }


  @Override
  public void run() {
    Request request = null;
    try {
      RequestBody bodyRequest = RequestBody
          .create(Constants.JSON, baseApp.getObjectMapper().writeValueAsString(registrationDTO));
      request = new Request.Builder().url(REST_URL + CREATE_EP)
          .post(bodyRequest).build();
      SuccessDTO successDTO = loadDataFromServer(request);
      String email = registrationDTO.getEmail();
      String password = registrationDTO.getPassword();
      String firstName = registrationDTO.getFirstName();
      String lastName = registrationDTO.getLastName();

      LoginResponseDTO login = new LoginResponseDTO();
      login.setEmail(email);
      login.setPassword(password);
      Profile profile = new Profile();
      profile.setRestId(Long.parseLong(successDTO.getMessage()));
      profile.setPassword(password);
      profile.setEmail(email);
      profile.setFirstName(firstName);
      profile.setLastName(lastName);

      baseApp.getDB().profileDAO().create(profile);
      EventBus.getDefault().post(new SuccessRegisterEvent(this.getClass().getSimpleName()));
    } catch (ServerException e) {
      EventBus.getDefault().post(new FailedRegisterEvent(0, e.getErrorDTO().getDetailedMessage(),
          this.getClass().getSimpleName()));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}