package com.omisoft.hsracer.common.utils;



import android.util.Log;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.events.ErrorEvent;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.greenrobot.eventbus.EventBus;

/**
 * Logs requests
 * Created by dido on 19.06.17.
 **/
public class LoggingInterceptor implements Interceptor {

  public static final String TAG = LoggingInterceptor.class.getName();

  private static final String F_BREAK = " %n";
  private static final String F_URL = " %s";
  private static final String F_TIME = " in %.1fms";
  private static final String F_HEADERS = "%s";
  private static final String F_RESPONSE = F_BREAK + "Response: %d";
  private static final String F_BODY = "body: %s";

  private static final String F_BREAKER =
      F_BREAK + "-------------------------------------------" + F_BREAK;
  private static final String F_REQUEST_WITHOUT_BODY = F_URL + F_TIME + F_BREAK + F_HEADERS;
  private static final String F_RESPONSE_WITHOUT_BODY =
      F_RESPONSE + F_BREAK + F_HEADERS + F_BREAKER;
  private static final String F_REQUEST_WITH_BODY =
      F_URL + F_TIME + F_BREAK + F_HEADERS + F_BODY + F_BREAK;
  private static final String F_RESPONSE_WITH_BODY =
      F_RESPONSE + F_BREAK + F_HEADERS + F_BODY + F_BREAK + F_BREAKER;

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Log.wtf(TAG, "intercept: ");
    long t1 = System.nanoTime();
    Response response = chain.proceed(request);
    long t2 = System.nanoTime();

    MediaType contentType = null;
    String bodyString = null;
    if (response.body() != null) {
      contentType = response.body().contentType();
      bodyString = response.body().string();
    }

    double time = (t2 - t1) / 1e6d;

    switch (request.method()) {
      case "GET":
        Log.d(TAG, String
            .format("GET " + F_REQUEST_WITHOUT_BODY + F_RESPONSE_WITH_BODY, request.url(), time,
                request.headers(), response.code(), response.headers(),
                stringifyResponseBody(bodyString)));
        break;
      case "POST":
        Log.d(TAG, String
            .format("POST " + F_REQUEST_WITH_BODY + F_RESPONSE_WITH_BODY, request.url(), time,
                request.headers(), stringifyRequestBody(request), response.code(),
                response.headers(),
                stringifyResponseBody(bodyString)));
        break;
      case "PUT":
        Log.d(TAG, String
            .format("PUT " + F_REQUEST_WITH_BODY + F_RESPONSE_WITH_BODY, request.url(), time,
                request.headers(), request.body().toString(), response.code(), response.headers(),
                stringifyResponseBody(bodyString)));
        break;
      case "DELETE":
        Log.d(TAG, String
            .format("DELETE " + F_REQUEST_WITHOUT_BODY + F_RESPONSE_WITHOUT_BODY, request.url(),
                time,
                request.headers(), response.code(), response.headers()));
        break;
    }

    if (response.body() != null) {
      ResponseBody body = ResponseBody.create(contentType, bodyString);
      return response.newBuilder().body(body).build();
    } else {
      return response;
    }
  }


  private static String stringifyRequestBody(Request request) {
    try {
      final Request copy = request.newBuilder().build();
      final Buffer buffer = new Buffer();
      copy.body().writeTo(buffer);
      return buffer.readUtf8();
    } catch (final IOException e) {
      EventBus.getDefault()
          .post(new ErrorEvent(R.string.error_json, e, LoggingInterceptor.class.getSimpleName()));
      return null;
    }
  }

  public String stringifyResponseBody(String responseBody) {
    return responseBody;
  }
}