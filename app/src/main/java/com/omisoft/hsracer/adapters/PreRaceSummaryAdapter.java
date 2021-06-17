package com.omisoft.hsracer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.holders.PreRaceSummaryHolder;
import com.omisoft.hsracer.ws.protocol.dto.RacerSummary;
import com.omisoft.hsracer.ws.protocol.enums.PlayerStatus;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by developer on 02.10.17.
 */

public class PreRaceSummaryAdapter extends
    RecyclerView.Adapter<PreRaceSummaryHolder> {

  @Getter
  @Setter
  private List<RacerSummary> mListWithRacerStatus;

  public PreRaceSummaryAdapter(List<RacerSummary> racerSummaryList) {
    this.mListWithRacerStatus = racerSummaryList;
  }

  @Override
  public PreRaceSummaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    View view = layoutInflater
        .inflate(R.layout.fragment_recyclerview_preracesummary, parent, false);
    return new PreRaceSummaryHolder(view);
  }

  @Override
  public void onBindViewHolder(PreRaceSummaryHolder holder, int position) {
    holder.getMNickName().setText(mListWithRacerStatus.get(position).getRacerNickname());
    holder.getMCarName()
        .setText(String.format("(%s)", mListWithRacerStatus.get(position).getCarName()));
    if (mListWithRacerStatus.get(position).getPlayerStatus() != PlayerStatus.READY) {
      holder.getMRacerStatus().setImageResource(R.drawable.not_ready_user_icon);
      holder.getMStatusText().setText(R.string.not_ready);
    } else {
      holder.getMRacerStatus().setImageResource(R.drawable.ready_user_icon);
      holder.getMStatusText().setText(R.string.ready);
    }
  }

  @Override
  public int getItemCount() {
    return mListWithRacerStatus.size();
  }

}
