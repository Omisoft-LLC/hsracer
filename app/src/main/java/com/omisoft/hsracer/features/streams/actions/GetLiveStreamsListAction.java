package com.omisoft.hsracer.features.streams.actions;

import static com.omisoft.hsracer.constants.URLConstants.AUTHORIZATION_HEADER;
import static com.omisoft.hsracer.constants.URLConstants.REST_URL;
import static com.omisoft.hsracer.constants.URLConstants.STREAMS;

import android.net.Uri;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.events.ErrorEvent;
import com.omisoft.hsracer.common.loader.BaseServerAction;
import com.omisoft.hsracer.dto.DataFromServerDTO;
import com.omisoft.hsracer.dto.StreamsPaginationDTO;
import com.omisoft.hsracer.exceptions.ServerException;
import com.omisoft.hsracer.features.streams.events.SuccessStreamsListEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import okhttp3.Request;
import org.greenrobot.eventbus.EventBus;

/**
 * Get results from server
 * Created by dido on 08.06.17.
 */

public class GetLiveStreamsListAction extends BaseServerAction<DataFromServerDTO> implements
    Runnable {

  private static final String TAG = GetLiveStreamsListAction.class.getName();
  private final BaseApp baseApp;
  private String page;
  private String numberOfItems;

  //  , String page, String numberOfItems
  public GetLiveStreamsListAction(BaseApp baseApp, String page, String numberOfItems) {
    super(baseApp, DataFromServerDTO.class);
    this.baseApp = baseApp;
    this.page = page;
    this.numberOfItems = numberOfItems;
  }


  @Override
  public void run() {
    Uri.Builder uri = Uri.parse(REST_URL + STREAMS).buildUpon();
    uri.appendQueryParameter("page", page).appendQueryParameter("per_page", numberOfItems).build();
    URL url = null;
    try {
      url = new URL(uri.toString());
    } catch (MalformedURLException e) {
      Log.e(TAG, "run: ", e);
    }
    Request request = new Request.Builder().url(url)
        .get()
        .addHeader(AUTHORIZATION_HEADER, baseApp.getAuthId()).build();
    DataFromServerDTO data = null;
    try {
      data = loadRawDataFromServer(request);
      if (data != null && data.getResponseCode() == 200) {
        ObjectMapper mapper = getMapper();
        Log.wtf(TAG, "run: " + data.getBody());
        StreamsPaginationDTO results = mapper
            .readValue(data.getBody(), StreamsPaginationDTO.class);
        EventBus.getDefault().postSticky(
            new SuccessStreamsListEvent(results.getData(),
                results.getLast_page(), results.getCurrent_page()));
      }
    } catch (ServerException e) {
      Log.e(TAG, "run: ", e);
      baseApp.getEventBus().post(e);
    } catch (IOException e) {
      Log.e(TAG, "run: ", e);
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.server_error, e, this.getClass().getSimpleName()));
    } catch (Exception e) {
      Log.e(TAG, "run: ", e);
    }
  }
}
