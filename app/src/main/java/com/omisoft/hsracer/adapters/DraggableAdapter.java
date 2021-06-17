package com.omisoft.hsracer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.utils.TimeUtils;
import com.omisoft.hsracer.ws.protocol.dto.RaceStatusDTO;
import com.omisoft.hsracer.ws.protocol.dto.RacerSummary;
import java.util.Collections;
import java.util.List;

/**
 * An adapter used in race results
 * Created by Omisoft LLC. on 5/23/17.
 */

public class DraggableAdapter extends RecyclerView.Adapter<MyViewHolder> implements
    DraggableItemAdapter<MyViewHolder> {

  private final List<RacerSummary> mItems;


  public DraggableAdapter(RaceStatusDTO raceStatusDTO) {
    setHasStableIds(true); // this is required for D&D feature.
    List<RacerSummary> summaries = raceStatusDTO.getSummaryList();
    int i = 0;
    Collections.sort(summaries);
    for (RacerSummary r : summaries) {
      r.setId(i++);
    }
    mItems = summaries;
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.race_summary_row, parent, false);
    return new MyViewHolder(v);
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    RacerSummary item = mItems.get(position);
    holder.posView.setText(String.valueOf(item.getPosition()));
    holder.aliasView.setText(item.getAlias());
    holder.carView.setText(item.getCarName());
    holder.timeView.setText(TimeUtils.formatInterval(item.getT()));
    holder.statusView.setText(String.valueOf(item.getPlayerStatus()));
  }

  @Override
  public long getItemId(int position) {
    return mItems.get(position)
        .getId(); // need to return stable (= not change even after reordered) value
  }

  @Override
  public int getItemCount() {
    return mItems.size();
  }

  @Override
  public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
    return true;
  }

  @Override
  public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
    return null;
  }

  @Override
  public void onMoveItem(int fromPosition, int toPosition) {
    RacerSummary movedItem = mItems.remove(fromPosition);
    mItems.add(toPosition, movedItem);
    notifyItemMoved(fromPosition, toPosition);
  }

  @Override
  public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
    return true;
  }
}

/**
 * Helper class for DraggableAdapter, initializes the adapter's views (holder)
 */
class MyViewHolder extends AbstractDraggableItemViewHolder {

  final TextView posView;
  final TextView aliasView;
  final TextView carView;
  final TextView timeView;
  final TextView statusView;

  public MyViewHolder(View itemView) {
    super(itemView);
    posView = itemView.findViewById(R.id.pos_race_summary);
    aliasView = itemView.findViewById(R.id.alias_race_summary);
    carView = itemView.findViewById(R.id.car_race_summary);
    timeView = itemView.findViewById(R.id.time_race_summary);
    statusView = itemView.findViewById(R.id.status_race_summary);
  }

}


