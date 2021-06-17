package com.omisoft.hsracer.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.features.profile.interfaces.ClickRecycleItemInterface;
import lombok.Getter;
import lombok.Setter;

/**
 * Cars holder
 * Created by developer on 04.10.17.
 */
@Getter
@Setter
public class CarsHolder extends RecyclerView.ViewHolder {

  private ImageButton mDeleteCarImageButton;
  private TextView mCarName;
  public TextView mMakeAndCarModelName;
  private RelativeLayout mRelativeLayout;

  /**
   * Holder that init the views from fragment_car_names
   */
  public CarsHolder(View itemView, final ClickRecycleItemInterface mCarsInterface) {
    super(itemView);
    mCarName = itemView.findViewById(R.id.car_name_in_cars);
    mMakeAndCarModelName = itemView.findViewById(R.id.make_name_and_model_in_cars);
    mRelativeLayout = itemView.findViewById(R.id.fragment_car_name_root);
    mDeleteCarImageButton = itemView.findViewById(R.id.ib_delete_vehicle_in_cars);
    mDeleteCarImageButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mCarsInterface.deleteVehicle(getAdapterPosition());
      }
    });
    mRelativeLayout.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        mCarsInterface.clickOnView(view, getAdapterPosition());
      }
    });

  }

}
