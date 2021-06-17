package com.omisoft.hsracer.features.share;

import android.content.Context;
import android.content.ContextWrapper;

/** Audio Service provider
 * Created by developer on 27.09.17.
 */

public class AudioServiceContext extends ContextWrapper {

  public AudioServiceContext(Context base) {
    super(base);
  }

  public static ContextWrapper getContext(Context base) {
    return new AudioServiceContext(base);
  }

  @Override
  public Object getSystemService(String name) {
    if (Context.AUDIO_SERVICE.equals(name)) {
      return getApplicationContext().getSystemService(name);
    }
    return super.getSystemService(name);
  }
}
