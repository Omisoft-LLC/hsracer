package com.omisoft.hsracer.features.streams.actions;

import static com.omisoft.hsracer.constants.URLConstants.AUTHORIZATION_HEADER;
import static com.omisoft.hsracer.constants.URLConstants.REST_URL;
import static com.omisoft.hsracer.constants.URLConstants.STREAMS;
import static com.omisoft.hsracer.constants.URLConstants.VIEW;

import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.loader.BaseServerAction;
import com.omisoft.hsracer.dto.StreamDTO;
import com.omisoft.hsracer.exceptions.ServerException;
import com.omisoft.hsracer.features.streams.events.SuccessStreamDTOEvent;
import com.omisoft.hsracer.utils.Utils;
import com.yakivmospan.scytale.Store;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.greenrobot.eventbus.EventBus;

/**
 * Return profile from server
 * Created by dido on 13.06.17.
 */

public class GetStreamsForRaceAction extends BaseServerAction<StreamDTO> implements
    Closeable {

  private final BaseApp baseApp;
  private static final String TAG = GetStreamsForRaceAction.class.getName();
  private final Long raceId;

  public GetStreamsForRaceAction(BaseApp context, Long raceId) {
    super(context, StreamDTO.class);
    this.baseApp = context;
    this.raceId = raceId;
  }

  @Override
  public void run() {
    Store store = new Store(Utils.getApp());

    HttpUrl.Builder httpBuider = HttpUrl.parse(REST_URL + STREAMS + VIEW).newBuilder()
        .addQueryParameter("raceId", String.valueOf(raceId));
    Request request = new Request.Builder().url(httpBuider.build())
        .addHeader(AUTHORIZATION_HEADER, baseApp.getAuthId()).get().build();

    try {
      List<StreamDTO> streamsList = loadCollectionFromServer(request);

        EventBus.getDefault().post(new SuccessStreamDTOEvent(streamsList));


    } catch (ServerException e) {
      EventBus.getDefault().post(e);

    }

  }


  @Override
  public void close() throws IOException {
  }
}
