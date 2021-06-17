package com.omisoft.hsracer.features.share.actions;

import static com.omisoft.hsracer.constants.URLConstants.AUTHORIZATION_HEADER;
import static com.omisoft.hsracer.constants.URLConstants.PUBLISH;
import static com.omisoft.hsracer.constants.URLConstants.REST_URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.common.loader.BaseServerAction;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dto.DataFromServerDTO;
import com.omisoft.hsracer.dto.PublishDTO;
import com.omisoft.hsracer.dto.PublishResultDTO;
import com.omisoft.hsracer.exceptions.ServerException;
import com.omisoft.hsracer.features.share.events.FailedToShareResultsEvent;
import com.omisoft.hsracer.features.share.events.ShareEvent;
import java.io.IOException;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.EventBus;

/** Shares the results to the server
 * Created by developer on 07.07.17.
 */

public class ShareServerAction extends BaseServerAction<DataFromServerDTO> {

  private static final String TAG = ShareServerAction.class.getName();
  private final BaseApp baseApp;
  private final PublishDTO publishDTO;

  public ShareServerAction(BaseApp context, PublishDTO publishDTO) {
    super(context, DataFromServerDTO.class);
    this.baseApp = context;
    this.publishDTO = publishDTO;
  }

  @Override
  public void run() {
    ObjectMapper mapper = getMapper();
    RequestBody bodyRequest;
    try {
      bodyRequest = RequestBody.create(Constants.JSON, mapper.writeValueAsString(publishDTO));
      Request request = new Builder().url(REST_URL + PUBLISH)
          .put(bodyRequest)
          .addHeader(AUTHORIZATION_HEADER, baseApp.getAuthId()).build();
      DataFromServerDTO data = loadRawDataFromServer(request);
      if (data != null && data.getResponseCode() == 200) {
        PublishResultDTO publishResultDTO = mapper
            .readValue(data.getBody(), PublishResultDTO.class);
        EventBus.getDefault()
            .post(new ShareEvent(publishResultDTO, this.getClass().getSimpleName()));
      }
    } catch (ServerException e) {
      EventBus.getDefault()
          .post(new FailedToShareResultsEvent(e.getErrorDTO().getDetailedMessage()));
    } catch (JsonProcessingException e) {
      baseApp.getEventBus().post(new ServerException(e));
    } catch (IOException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.server_error, e, this.getClass().getSimpleName()));
    }
  }
}
