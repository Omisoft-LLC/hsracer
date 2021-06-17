package com.omisoft.hsracer.adapters.callbacks;

import android.view.View;

/**
 * Created by developer on 07.11.17.
 */

public interface VideoViewInterface {

  void onItemClicked(View view, int position);

  void selectedItem(int position, boolean checked);
}
