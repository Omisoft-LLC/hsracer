package com.omisoft.hsracer.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.omisoft.hsracer.R;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by developer on 04.10.17.
 */

@Getter
@Setter
public class PreRaceSummaryHolder extends RecyclerView.ViewHolder {

  private TextView mNickName;
  private TextView mCarName;
  private ImageView mRacerStatus;
  private TextView mStatusText;
  private LinearLayout mBackgroundPanel;
  private RelativeLayout mFrontPanel;

  /**
   * Holder that init the views from fragment_recyclerview_preraceSummary
   */
  public PreRaceSummaryHolder(View itemView) {
    super(itemView);
    mNickName = itemView.findViewById(R.id.racer_nickname_in_creator);
    mCarName = itemView.findViewById(R.id.racer_car_name_in_creator);
    mRacerStatus = itemView.findViewById(R.id.status_image);
    mStatusText = itemView.findViewById(R.id.status_text);
    mBackgroundPanel = itemView.findViewById(R.id.background_panel);
    mFrontPanel = itemView.findViewById(R.id.front_panel);
  }
}
