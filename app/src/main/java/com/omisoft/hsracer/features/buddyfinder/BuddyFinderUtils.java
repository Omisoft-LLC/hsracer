package com.omisoft.hsracer.features.buddyfinder;

import com.omisoft.hsracer.features.buddyfinder.dto.JoinFinderDTO;
import java.util.List;
import org.osmdroid.util.BoundingBoxE6;

/**
 * Static utilities for buddy finder
 * Created by Omisoft LLC. on 6/9/17.
 */

public class BuddyFinderUtils {

  private static final String TAG = BuddyFinderUtils.class.getName();
  private final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;


  /**
   * Calculate distance in Haversine - https://en.wikipedia.org/wiki/Haversine_formula
   */
  public static int calculateDistanceInKilometer(double userLat, double userLng,
      double venueLat, double venueLng) {

    double latDistance = Math.toRadians(userLat - venueLat);
    double lngDistance = Math.toRadians(userLng - venueLng);

    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
        * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return (int) (AVERAGE_RADIUS_OF_EARTH_KM * c * 1000);
  }

  //public static long getRandomIdNumber(){
  //  return new Random().nextInt(1000) + 1;
  //}

  public static BoundingBoxE6 getBoundingBox(List<JoinFinderDTO> racers) {
    double north = 0;
    double east = 0;
    double south = 0;
    double west = 0;

    for (int i = 0; i < racers.size(); i++) {

      double lat = racers.get(i).getLatitude();
      double lon = racers.get(i).getLongitude();

      if ((i == 0) || (lat > north)) {
        north = lat;
      }
      if ((i == 0) || (lat < south)) {
        south = lat;
      }
      if ((i == 0) || (lon < west)) {
        west = lon;
      }
      if ((i == 0) || (lon > east)) {
        east = lon;
      }

    }
    return new BoundingBoxE6(north, east, south, west);
  }
}
