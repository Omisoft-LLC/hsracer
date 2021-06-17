package com.omisoft.hsracer.common.loader;

import android.util.Log;
import android.view.View;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.events.ProgressBarEvent;
import com.omisoft.hsracer.constants.URLConstants;
import com.omisoft.hsracer.dto.DataFromServerDTO;
import com.omisoft.hsracer.dto.ErrorDTO;
import com.omisoft.hsracer.exceptions.ServerException;
import java.io.IOException;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.greenrobot.eventbus.EventBus;

/**
 * Generic server action
 * Created by dido on 07.06.17.
 */

public abstract class BaseServerAction<T> implements Runnable {

  protected String TAG;
  private final Class<T> typeParameterClass;
  private final BaseApp baseApp;

  public BaseServerAction(BaseApp context, Class<T> typeParameterClass) {
    this.TAG = this.getClass().getSimpleName();
    this.typeParameterClass = typeParameterClass;
    baseApp = context;
  }


  /**
   * Loads data from server
   */
  protected T loadDataFromServer(Request request) throws ServerException {

    try {
      showOrHideProgressOverlay(View.VISIBLE);
      Response response = getClient().newCall(request).execute();
      ResponseBody responseBody = response.body();
      int responseCode = response.code();
      if (responseCode == 200 && responseBody != null) {
        showOrHideProgressOverlay(View.GONE);
        return getMapper().readValue(responseBody.string(), typeParameterClass);
      } else {
        showOrHideProgressOverlay(View.GONE);
        String body = response.body().string();
        ErrorDTO errorDTO = getMapper().readValue(body, ErrorDTO.class);
        throw new ServerException(response.code(), errorDTO.getDetailedMessage());
      }
    } catch (IOException e) {
      showOrHideProgressOverlay(View.GONE);
      Log.wtf(TAG, "loadRawDataFromServer: 2", e);
      throw new ServerException(new ErrorDTO("Server Error - 500", "Server Error"));
    }
  }


  protected List<T> loadCollectionFromServer(Request request) throws ServerException {

    try {
      showOrHideProgressOverlay(View.VISIBLE);
      Response response = getClient().newCall(request).execute();
      ResponseBody responseBody = response.body();
      int responseCode = response.code();
      if (responseCode == 200 && responseBody != null) {
        showOrHideProgressOverlay(View.GONE);
        return getMapper().readValue(responseBody.string(),
            getMapper().getTypeFactory().constructCollectionType(List.class, typeParameterClass));
      } else {
        showOrHideProgressOverlay(View.GONE);
        String body = response.body().string();
        ErrorDTO errorDTO = getMapper().readValue(body, ErrorDTO.class);
        throw new ServerException(response.code(), errorDTO.getDetailedMessage());
      }
    } catch (IOException e) {
      showOrHideProgressOverlay(View.GONE);
      Log.wtf(TAG, "loadRawDataFromServer: 2", e);
      throw new ServerException(new ErrorDTO("Server Error - 500", "Server Error"));
    }
  }

  public OkHttpClient getClient() {
    return baseApp.getHttpClient();
  }

  protected ObjectMapper getMapper() {
    return baseApp.getObjectMapper();

  }

  protected DataFromServerDTO loadRawDataFromServer(Request request)
      throws ServerException {
    Response response;
    try {
      showOrHideProgressOverlay(View.VISIBLE);
      response = getClient().newCall(request).execute();
      if (response != null) {
        if (response.code() == 200 && response.body() != null) {
          showOrHideProgressOverlay(View.GONE);
          String body = response.body().string();
          DataFromServerDTO dto = new DataFromServerDTO();
          dto.setBody(body);
          dto.setResponseCode(response.code());
          dto.setAuthId(response.header("authorization"));
          dto.setAesKey(response.header(URLConstants.KEY));
          return dto;
        } else {
          showOrHideProgressOverlay(View.GONE);
          String body = response.body().string();
          ErrorDTO errorDTO = getMapper().readValue(body, ErrorDTO.class);
          throw new ServerException(response.code(), errorDTO.getDetailedMessage());
        }
      } else {
        showOrHideProgressOverlay(View.GONE);
        return null;
      }
    } catch (IOException e) {
      Log.e(TAG, "loadRawDataFromServer: ", e);
      showOrHideProgressOverlay(View.GONE);
      throw new ServerException(new ErrorDTO("Server Error - 500", "Server Error"));
    }
  }

  private void showOrHideProgressOverlay(int visible) {
    EventBus.getDefault()
        .post(new ProgressBarEvent(visible, this.getClass().getSimpleName()));
  }

}