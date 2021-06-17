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
import com.omisoft.hsracer.dto.RaceResultDTO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 * Adapter that obtains the results from all of the races made for the current account
 * Created by Omisoft LLC. on 6/6/17.
 */

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolderResult> {

  @Getter
  @Setter
  private ArrayList<RaceResultDTO> mRacesResults;

  private final SimpleDateFormat SIMPLE_DATE_FORMATTER = new SimpleDateFormat(" MMM d");
  private ResultViewInterface mInterface;

  public ResultsAdapter(ArrayList<RaceResultDTO> races, ResultViewInterface resultViewInterface) {
    this.mRacesResults = races;
    this.mInterface = resultViewInterface;
  }


  @Override
  public ViewHolderResult onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.results_list_view_row, parent, false);
    return new ViewHolderResult(view);
  }

  @Override
  public void onBindViewHolder(ViewHolderResult holder, int position) {
    holder.mRaceName.setText(mRacesResults.get(position).getName());
    int positionInRace = mRacesResults.get(position).getFinishPosition();
    switch (positionInRace) {
      case 1:
        holder.mPosition
            .setText(String.valueOf(positionInRace + "st"));
        break;
      case 2:
        holder.mPosition
            .setText(String.valueOf(positionInRace + "nd"));
        break;
      case 3:
        holder.mPosition
            .setText(String.valueOf(positionInRace + "rd"));
      default:
        holder.mPosition
            .setText(String.valueOf(positionInRace + "th"));
    }
    holder.mRaceDate.setText(
        String.valueOf(SIMPLE_DATE_FORMATTER.format(mRacesResults.get(position).getStartDate())));
  }

  @Override
  public int getItemCount() {
    if (mRacesResults != null) {
      return mRacesResults.size();
    } else {
      return 0;
    }
  }


  /**
   * Helper class for ResultsAdapter, initializes the adapter's views (holder)
   */
  class ViewHolderResult extends RecyclerView.ViewHolder {

    final TextView mRaceName;
    final TextView mPosition;
    final TextView mRaceDate;
    final RelativeLayout mItemLayout;

    ViewHolderResult(View itemView) {
      super(itemView);
      mRaceName = itemView.findViewById(R.id.race_name_text_view);
      mPosition = itemView.findViewById(R.id.position_result_text_view);
      mRaceDate = itemView.findViewById(R.id.date_result_text_view);
      mItemLayout = itemView.findViewById(R.id.ll_item_for_result);
      mItemLayout.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          mInterface.clickedView(v, getAdapterPosition());
        }
      });
    }
  }
}
