package com.omisoft.hsracer.common.utils;

import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.events.ErrorEvent;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.greenrobot.eventbus.EventBus;

/**
 * Authorization Interceptor
 * Created by developer on 20.11.17.
 */

public class AuthorizationInterceptor implements Interceptor {

  private static final String TAG = AuthorizationInterceptor.class.getName();

  public AuthorizationInterceptor() {

  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Response response = chain.proceed(request);
    if (response.code() == 401) {
      EventBus.getDefault().post(new ErrorEvent(R.string.server_error_authorization,
          AuthorizationInterceptor.class.getName()));
      return null;
    }
    return response;
  }
}
