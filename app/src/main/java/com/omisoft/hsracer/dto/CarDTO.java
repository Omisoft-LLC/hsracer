package com.omisoft.hsracer.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.omisoft.hsracer.model.FuelType;
import com.omisoft.hsracer.model.VehicleType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Car DTO used for CRUD operations on Car
 * Created by dido on 06.04.17.
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarDTO implements Parcelable {

  public static final Creator<CarDTO> CREATOR = new Creator<CarDTO>() {
    @Override
    public CarDTO createFromParcel(Parcel in) {
      return new CarDTO(in);
    }

    @Override
    public CarDTO[] newArray(int size) {
      return new CarDTO[size];
    }
  };
  private String alias;
  private String make;
  private String model;
  private String year;
  private FuelType fuel;
  private String engineCylinders;
  private String volume;
  private Integer hpr;
  private Long restId;
  private VehicleType vehicleType;

  public CarDTO() {
  }


  protected CarDTO(Parcel in) {
    alias = in.readString();
    make = in.readString();
    model = in.readString();
    year = in.readString();
    fuel = FuelType.valueOf(in.readString());
    engineCylinders = in.readString();
    volume = in.readString();
    hpr = in.readInt();
    restId = in.readLong();
    vehicleType = VehicleType.valueOf(in.readString());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(alias);
    dest.writeString(make);
    dest.writeString(model);
    dest.writeString(year);
    dest.writeString(fuel.name());
    dest.writeString(engineCylinders);
    dest.writeString(volume);
    dest.writeInt(hpr);
    dest.writeLong(restId);
    dest.writeString(vehicleType.name());
  }
}
