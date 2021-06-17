package com.omisoft.hsracer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.features.profile.interfaces.ClickRecycleItemInterface;
import com.omisoft.hsracer.holders.CarsHolder;
import com.omisoft.hsracer.model.Car;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Created to show race in CarActivity.
 * Created by developer on 02.10.17.
 */

public class CarsAdapter extends RecyclerView.Adapter<CarsHolder> {

  private ClickRecycleItemInterface mCarsInterface;
  @Getter
  @Setter
  private List<Car> mListWithCars;

  public CarsAdapter(List<Car> listWithCars, ClickRecycleItemInterface carsInterface) {
    this.mListWithCars = listWithCars;
    this.mCarsInterface = carsInterface;
  }

  @Override
  public CarsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    View view = layoutInflater.inflate(R.layout.fragment_car_names, parent, false);
    return new CarsHolder(view, mCarsInterface);
  }

  @Override
  public void onBindViewHolder(CarsHolder holder, int position) {
    holder.getMCarName().setText(mListWithCars.get(position).getAlias());
    holder.getMMakeAndCarModelName()
        .setText(String.format("%s - %s", mListWithCars.get(position).getManufacturer(),
            mListWithCars.get(position).getModel()));
  }

  @Override
  public int getItemCount() {
    return mListWithCars.size();
  }

}
