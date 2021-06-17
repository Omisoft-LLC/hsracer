package com.omisoft.hsracer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Car info
 * Created by dido on 20.04.17.
 */

@Entity(foreignKeys = @ForeignKey(entity = Profile.class, parentColumns = "_id", childColumns = "profile_id"), indices = {
    @Index("profile_id"), @Index("rest_id")})
public class Car extends BaseEntity implements Parcelable {

  public static final int DEFAULT_BHP = 0;
  public static final String DEFAULT_VOLUME = "0";
  private String manufacturer;

  private String alias;

  private String model;

  private int year;

  private String engineCylinders;

  private String volume;

  private int hpr;

  private FuelType fuel;
  @ColumnInfo(name = "profile_id")
  private Long profileId;
  @ColumnInfo(name = "rest_id")
  private Long restId;

  private VehicleType vehicleType;

  public Car() {

  }


  protected Car(Parcel in) {
    Class c = byte[].class;
    manufacturer = in.readString();
    alias = in.readString();
    model = in.readString();
    year = in.readInt();
    engineCylinders = in.readString();
    volume = in.readString();
    hpr = in.readInt();
    fuel = FuelType.valueOf(in.readString());
    profileId = in.readLong();
    restId = in.readLong();
    vehicleType = VehicleType.valueOf(in.readString());
  }

  public static final Creator<Car> CREATOR = new Creator<Car>() {
    @Override
    public Car createFromParcel(Parcel in) {
      return new Car(in);
    }

    @Override
    public Car[] newArray(int size) {
      return new Car[size];
    }
  };

  public ContentValues toContentValues() {
    return null;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(manufacturer);
    dest.writeString(alias);
    dest.writeString(model);
    dest.writeInt(year);
    dest.writeString(engineCylinders);
    dest.writeString(volume);
    dest.writeInt(hpr);
    dest.writeString(fuel.name());
    dest.writeLong(profileId);
    dest.writeLong(restId);
    dest.writeString(vehicleType.name());

  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public String getEngineCylinders() {
    return engineCylinders;
  }

  public void setEngineCylinders(String engineCylinders) {
    this.engineCylinders = engineCylinders;
  }

  public String getVolume() {
    return volume;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  public Integer getHpr() {
    return hpr;
  }

  public void setHpr(Integer hpr) {
    this.hpr = hpr;
  }

  public FuelType getFuel() {
    return fuel;
  }

  public void setFuel(FuelType fuel) {
    this.fuel = fuel;
  }

  public Long getProfileId() {
    return profileId;
  }

  public void setProfileId(Long profileId) {
    this.profileId = profileId;
  }

  public Long getRestId() {
    return restId;
  }

  public void setRestId(Long restId) {
    this.restId = restId;
  }

  public VehicleType getVehicleType() {
    return vehicleType;
  }

  public void setVehicleType(VehicleType vehicleType) {
    this.vehicleType = vehicleType;
  }

  @Override
  public String toString() {
    return "Car{" +
        "manufacturer='" + manufacturer + '\'' +
        ", alias='" + alias + '\'' +
        ", model='" + model + '\'' +
        ", year=" + year +
        ", engineCylinders='" + engineCylinders + '\'' +
        ", volume='" + volume + '\'' +
        ", hpr=" + hpr +
        ", fuel=" + fuel +
        ", profileId=" + profileId +
        ", restId=" + restId +
        ", vehicleType='" + vehicleType + '\'' +
        '}';
  }
}
