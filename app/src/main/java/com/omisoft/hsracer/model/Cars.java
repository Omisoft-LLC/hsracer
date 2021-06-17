package com.omisoft.hsracer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Cars Entity
 * Created by developer on 01.12.17.
 */
@Entity(tableName = "cars_ref", indices = {
    @Index(value = "make", name = "make_idx")})
public class Cars extends BaseEntity implements Parcelable {


  @ColumnInfo(name = "make")
  private String make;

  @ColumnInfo(name = "model")
  private String model;

  @ColumnInfo(name = "type")
  private String type;

  protected Cars(Parcel in) {
    make = in.readString();
    model = in.readString();
    type = in.readString();
  }

  public Cars() {
  }

  public static final Creator<Cars> CREATOR = new Creator<Cars>() {
    @Override
    public Cars createFromParcel(Parcel in) {
      return new Cars(in);
    }

    @Override
    public Cars[] newArray(int size) {
      return new Cars[size];
    }
  };

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getMake() {
    return make;
  }

  public void setMake(String make) {
    this.make = make;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(make);
    dest.writeString(model);
    dest.writeString(type);
  }
}
