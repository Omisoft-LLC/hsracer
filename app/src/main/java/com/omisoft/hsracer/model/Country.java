package com.omisoft.hsracer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

/**
 * Country Entity
 * Created by developer on 01.12.17.
 */
@Entity(tableName = "countries_ref")
public class Country extends BaseEntity {

  @ColumnInfo(name = "countries")
  private String countries;

  public String getCountries() {
    return countries;
  }

  public void setCountries(String countries) {
    this.countries = countries;
  }

  @Override
  public String toString() {
    return "Country{" +
        "country='" + countries + '\'' +
        '}';
  }
}
