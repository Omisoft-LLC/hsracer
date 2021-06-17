package com.omisoft.hsracer.db;

import android.arch.persistence.room.TypeConverter;
import com.omisoft.hsracer.model.FuelType;
import com.omisoft.hsracer.model.VehicleType;
import com.omisoft.hsracer.ws.protocol.enums.RaceType;
import java.util.Date;

/**
 * DB Type Converters
 * Created by dido on 08.06.17.
 */

public class Converters {

  @TypeConverter
  public static Date fromTimestamp(Long value) {
    return value == null ? null : new Date(value);
  }

  @TypeConverter
  public static Long dateToTimestamp(Date date) {
    return date == null ? null : date.getTime();
  }

  @TypeConverter
  public static FuelType fromString(String value) {
    return value == null ? null : FuelType.valueOf(value);
  }

  @TypeConverter
  public static String fuelTypeToString(FuelType data) {
    return data == null ? null : data.name();
  }

  @TypeConverter
  public static RaceType raceTypeFromString(String value) {
    return value == null ? null : RaceType.valueOf(value);
  }

  @TypeConverter
  public static String raceTypeToString(RaceType data) {
    return data == null ? null : data.name();
  }

  @TypeConverter
  public static VehicleType vehicleTypeFromString(String value) {
    return value == null ? null : VehicleType.valueOf(value);
  }

  @TypeConverter
  public static String vehicleTypeToString(VehicleType data) {
    return data == null ? null : data.name();
  }
}
