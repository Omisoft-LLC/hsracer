package com.omisoft.hsracer.features.race.async;

import static com.omisoft.hsracer.constants.URLConstants.URL_SHORTHENER_API;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.events.ProgressBarEvent;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.constants.URLConstants;
import com.omisoft.hsracer.features.race.PreRaceSummaryActivity;
import com.omisoft.hsracer.utils.AndroidUtils;
import com.omisoft.hsracer.utils.Utils;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Generates short url using Google URL Shortner
 * Created by dido on 05.02.18.
 */

public class URLShorthenerTask extends AsyncTask<URL, Void, String> {


  private static final String TAG = URLShorthenerTask.class.getSimpleName();
  private final WeakReference<PreRaceSummaryActivity> mpreRaceSummaryActivity;

  public URLShorthenerTask(
      WeakReference<PreRaceSummaryActivity> preRaceSummaryActivity) {
    mpreRaceSummaryActivity = preRaceSummaryActivity;

  }

  /**
   * Override this method to perform a computation on a background thread. The
   * specified parameters are the parameters passed to {@link #execute}
   * by the caller of this task.
   *
   * This method can call {@link #publishProgress} to publish updates
   * on the UI thread.
   *
   * @param urls The parameters of the task.
   * @return A result, defined by the subclass of this task.
   * @see #onPreExecute()
   * @see #onPostExecute
   * @see #publishProgress
   */
  @Override
  protected String doInBackground(URL... urls) {
    String json = "{\"longUrl\": \"" + urls[0].toString() + "\"}";
    Log.e(TAG, json);
    BaseApp baseApp = Utils.getBaseApp();
    String sig = AndroidUtils.getSignature(baseApp.getPackageManager(), BuildConfig.APPLICATION_ID);
    OkHttpClient httpClient = baseApp.getHttpClient();
    RequestBody bodyRequest = RequestBody
        .create(Constants.JSON, json);
    Request request = new Builder().url(
        URL_SHORTHENER_API)
        .addHeader(URLConstants.HEADER_ANDROID_PACKAGE, BuildConfig.APPLICATION_ID)
        .addHeader(URLConstants.HEADER_ANDROID_CERT, sig)
        .post(bodyRequest).build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (response.isSuccessful()) {
        JSONObject jsonObject = new JSONObject(response.body().string());
        return (String) jsonObject.get("id");
      } else {
        Log.e(TAG, response.body().string());
      }
    } catch (IOException | JSONException e) {
      e.printStackTrace();
    } finally {
      showOrHideProgressOverlay(View.GONE);

    }
    return null;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    showOrHideProgressOverlay(View.VISIBLE);
  }


  private void showOrHideProgressOverlay(int visible) {
    EventBus.getDefault()
        .post(new ProgressBarEvent(visible, this.getClass().getSimpleName()));
  }

  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);

    showOrHideProgressOverlay(View.GONE);
    Log.d(TAG, "URL:" + s);
    mpreRaceSummaryActivity.get().setShareURL(s);
  }
}
