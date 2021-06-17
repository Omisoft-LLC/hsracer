package com.omisoft.hsracer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.adapters.callbacks.ResultViewInterface;
import com.omisoft.hsracer.dto.StreamListDTO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 * Adapter that obtains the results from all of the races made for the current account
 * Created by Omisoft LLC. on 6/6/17.
 */

public class StreamsListAdapter extends RecyclerView.Adapter<StreamsListAdapter.ViewHolderResult> {

  @Getter
  @Setter
  private ArrayList<StreamListDTO> mStreamsList;

  private static final SimpleDateFormat SIMPLE_DATE_FORMATTER = new SimpleDateFormat(
      " MM/dd/yy HH:mm:ss");
  private ResultViewInterface mInterface;

  public StreamsListAdapter(ArrayList<StreamListDTO> races,
      ResultViewInterface resultViewInterface) {
    this.mStreamsList = races;
    this.mInterface = resultViewInterface;
  }


  @Override
  public ViewHolderResult onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.streams_list_view_row, parent, false);
    return new ViewHolderResult(view);
  }

  @Override
  public void onBindViewHolder(ViewHolderResult holder, int position) {
    holder.raceDescription.setText(mStreamsList.get(position).getRaceDesc());
    holder.mRaceId.setText(String.valueOf(mStreamsList.get(position).getRaceId()));
    holder.mStartLiveAt.setText(
        String
            .valueOf(SIMPLE_DATE_FORMATTER.format(mStreamsList.get(position).getStartStreamAt())));
  }

  @Override
  public int getItemCount() {
    if (mStreamsList != null) {
      return mStreamsList.size();
    } else {
      return 0;
    }
  }


  /**
   * Helper class for StreamsListAdapter, initializes the adapter's views (holder)
   */
  class ViewHolderResult extends RecyclerView.ViewHolder {

    final TextView raceDescription;
    final TextView mStartLiveAt;
    final TextView mRaceId;
    final RelativeLayout mItemLayout;

    ViewHolderResult(View itemView) {
      super(itemView);
      raceDescription = itemView.findViewById(R.id.streams_desc_text_view);
      mStartLiveAt = itemView.findViewById(R.id.streams_date_text_view);
      mRaceId = itemView.findViewById(R.id.streams_race_id_text_view);
      mItemLayout = itemView.findViewById(R.id.ll_item_for_stream);
      mItemLayout.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          mInterface.clickedView(v, getAdapterPosition());
        }
      });
    }
  }
}
