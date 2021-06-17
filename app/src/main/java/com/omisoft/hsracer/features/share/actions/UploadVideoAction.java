package com.omisoft.hsracer.features.share.actions;

import static com.omisoft.hsracer.common.BaseApp.get;
import static com.omisoft.hsracer.constants.Constants.CURRENT_VIDEO_FOR_UPLOADING;
import static com.omisoft.hsracer.constants.Constants.NUMBER_OF_VIDEOS;
import static com.omisoft.hsracer.constants.Constants.VIDEO_PREFERENCES;
import static com.omisoft.hsracer.constants.URLConstants.API_PUBLISH;
import static com.omisoft.hsracer.constants.URLConstants.REST_URL;
import static com.omisoft.hsracer.constants.URLConstants.UPLOAD;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.omisoft.hsracer.adapters.classes.UploadItemState;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.utils.NetworkUtils;
import com.omisoft.hsracer.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Action to upload the created video to server
 * Created by developer on 17.07.17.
 */

public class UploadVideoAction implements Runnable {

  private static final String TAG = UploadVideoAction.class.getName();

  private final Context context;
  public static final int UPLOAD_FAILED = 0;
  public static final int UPLOAD_SUCCESSFUL = 1;
  public static final int UPLOAD_PENDING = 2;
  public static final int UPLOAD_FAILED_DUE_TO_INTERNET = 3;

  @Getter
  private boolean running = true;
  private Handler handler;

  public UploadVideoAction(Context context, Handler handler) {
    this.context = context;
    this.handler = handler;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void run() {
    if (NetworkUtils.isWifiAvailable() || NetworkUtils.isDataAvailable()) {
      SharedPreferences sharedPreferences = context
          .getSharedPreferences(VIDEO_PREFERENCES, Context.MODE_PRIVATE);
      List<String> setWithVideoInfo = new ArrayList<>(
          sharedPreferences.getStringSet(Constants.VIDEOS_FOR_UPLOADING, new HashSet<String>()));
      int listSize = setWithVideoInfo.size();
      int currentElement = 0;
      if (listSize >= 1) {
        Bundle bundle = new Bundle();
        bundle.putInt(NUMBER_OF_VIDEOS, listSize);
        Message message = Message.obtain();
        message.what = UPLOAD_PENDING;
        message.setData(bundle);
        handler.sendMessage(message);

        for (String info : setWithVideoInfo) {
          try {
            currentElement++;
            UploadItemState itemState = get(context).getObjectMapper()
                .readValue(info, UploadItemState.class);
            File file = new File(itemState.getFullPathToVideo());
            if (file.exists()) {
              RequestBody bodyRequest;
              bodyRequest = new MultipartBody.Builder()
                  .setType(MultipartBody.FORM)
                  .addFormDataPart("racing_rest_id", String.valueOf(itemState.getRaceID()))
                  .addFormDataPart("name", itemState.getVideoName())
                  .addFormDataPart("file", null,
                      RequestBody
                          .create(MediaType.parse("application/octet-stream"), file))
                  .build();
              okhttp3.Request request = new okhttp3.Request.Builder()
                  .url(REST_URL + API_PUBLISH + UPLOAD)
                  .put(bodyRequest)
                  .addHeader(Constants.API_KEY, Utils.getBaseApp().getSharedPreferences().getString(Constants.API_KEY,"")).build();
              //TODO: 11-15 13:44:36.440 23281-24694/com.omisoft.hsracer:hsracerUploadProcess E/StrictMode: A resource was acquired at attached stack trace but never released. See java.io.Closeable for information on avoiding resource leaks.java.lang.Throwable: Explicit termination method 'response.body().close()' not calledat dalvik.system.CloseGuard.open(CloseGuard.java:180)at java.lang.reflect.Method.invoke(Native Method)at okhttp3.internal.platform.AndroidPlatform$CloseGuard.createAndOpen(AndroidPlatform.java:329)at okhttp3.internal.platform.AndroidPlatform.getStackTraceForCloseable(AndroidPlatform.java:144)at okhttp3.RealCall.captureCallStackTrace(RealCall.java:89)at okhttp3.RealCall.enqueue(RealCall.java:98)at com.omisoft.hsracer.features.share.actions.UploadVideoAction.run(UploadVideoAction.java:94)at java.lang.Thread.run(Thread.java:762)

              Response response = get(context).getHttpClient().newCall(request).execute();
              if (response.isSuccessful()) {
                if (response.code() != 200) {
                  bundle.putInt(CURRENT_VIDEO_FOR_UPLOADING, currentElement);
                  Message failureMessage = Message.obtain();
                  message.what = UPLOAD_FAILED;
                  message.setData(bundle);
                  handler.sendMessage(failureMessage);
                } else {
                  Log.i("UploadVideoService", "VIDEO COMPLETED");
                  bundle.putInt(CURRENT_VIDEO_FOR_UPLOADING, currentElement);
                  Message successMessage = Message.obtain();
                  message.setData(bundle);
                  message.what = UPLOAD_SUCCESSFUL;
                  handler.sendMessage(successMessage);
                }
              } else {
                Log.e(TAG, "RESPONSE IS NOT SUCCESSFUL");
              }
            }
          } catch (JsonParseException e) {
            e.printStackTrace();
          } catch (JsonMappingException e) {
            e.printStackTrace();
          } catch (IOException e) {
            bundle.putInt(CURRENT_VIDEO_FOR_UPLOADING, currentElement);
            Message failureMessage = Message.obtain();
            message.what = UPLOAD_FAILED_DUE_TO_INTERNET;
            message.setData(bundle);
            handler.sendMessage(failureMessage);
          } finally {
            if (setWithVideoInfo.size() != 0) {
              SharedPreferences sharedPreferences1 = context
                  .getSharedPreferences(VIDEO_PREFERENCES, Context.MODE_PRIVATE);
              Set<String> setWithItemToBeRemoved = new HashSet(sharedPreferences1
                  .getStringSet(Constants.VIDEOS_FOR_UPLOADING, new HashSet<String>()));
              setWithItemToBeRemoved.remove(info);
              SharedPreferences.Editor editor = sharedPreferences1.edit();
              editor.putStringSet(Constants.VIDEOS_FOR_UPLOADING, setWithItemToBeRemoved);
              if (editor.commit()) {
                Log.d(TAG, "WRITING IN THREAD");
              }
            }
          }
        }
      }
    }
  }
}

