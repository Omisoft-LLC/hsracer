package com.omisoft.hsracer.features.race.threads;

import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.features.race.events.RaceGPSEvent;
import com.omisoft.hsracer.features.race.structures.GPSDataDTO;
import com.omisoft.hsracer.utils.Utils;
import com.omisoft.hsracer.ws.protocol.dto.JoinedDTO;
import com.omisoft.hsracer.ws.protocol.enums.RaceType;
import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;
import lombok.Setter;
import org.greenrobot.eventbus.EventBus;

/**
 * Race GPS thread
 * Created by dido on 15.05.17.
 */

public class RaceGPSHandlerThread extends LongMonitoredThread {

  public static final int STOP_GETTING_GPS_DATA = 201;
  public static final int START_GETTING_GPS_DATA = 101;
  public static final int STOP_GPS = 1088;

  private static final String TAG = RaceGPSHandlerThread.class.getName();
  public static final int LOCATION_DATA = 301;
  private static final float FINISH_DISTANCE_GEOFENCE = 40;
  public static volatile Boolean raceRunning;
  private static Location prevLocation = null;
  private static Location currentLocation = null;
  @Getter
  private final BaseApp context;
  private volatile int totalDistance;

  private volatile long startTime;
  private volatile long totalTime;
  private static final BigDecimal SECOND_DIVIDER = new BigDecimal(1000);
  private static final BigDecimal TO_KPH = new BigDecimal(3.6);
  private RaceType raceType;
  public static volatile BigDecimal speed;
  @Getter
  private BlockingQueue<Message> commandQueue;
  @Getter
  @Setter
  private String name;
  private int raceFinishSpeed;
  private int raceDistance;
  private Location raceFinishLocation;


  public RaceGPSHandlerThread() {
    setName("Race GPS Thread");
    context = Utils.getBaseApp();
    commandQueue = new LinkedBlockingQueue<>();
  }

  public RaceGPSHandlerThread(RaceGPSHandlerThread original) {
    this.name = original.getName();
    this.commandQueue = original.getCommandQueue();
    context = original.getContext();

  }

  public void run() {
    threadState = ThreadState.RUNNING;

    boolean raceIsStarted = false;
    while (threadState == ThreadState.RUNNING) {
      Message message;
      try {
        message = commandQueue.take();
        if (message != null) {

          switch (message.what) {
            case START_GETTING_GPS_DATA:
              JoinedDTO joinedDTO = message.getData().getParcelable(JoinedDTO.class.getName());
              raceType = joinedDTO.getRaceType();
              raceDistance = joinedDTO.getDistance();
              raceFinishSpeed = joinedDTO.getEndSpeed();
              raceFinishLocation = new Location("");
              raceFinishLocation.setLatitude(joinedDTO.getFinishLat());
              raceFinishLocation.setLongitude(joinedDTO.getFinishLng());
              totalDistance = 0;
              totalTime = 0;
              prevLocation = null;
              raceIsStarted = true;
              break;
            case LOCATION_DATA:
              if (raceIsStarted) {
                Location location = message.getData().getParcelable(Location.class.getName());
                calculateGPSMeasurements(location);
              }
              break;
            case STOP_GETTING_GPS_DATA:
              threadState = ThreadState.FINISHED;
              break;
            default:
              break;
          }
        }
      } catch (Exception e) {
        threadState = ThreadState.CRASHED;
        return;
      }
    }
  }


  /**
   * This method calculates the data from the gps and stores it in the db and sends it to the
   * server, its used in both sprint and roll races.
   */
  private void calculateGPSMeasurements(Location location) {
    currentLocation = location;
    GPSDataDTO gpsDataDTO = new GPSDataDTO();
    gpsDataDTO.setLatitude(location.getLatitude());
    gpsDataDTO.setLongitude(location.getLongitude());
    if (prevLocation == null) {
      prevLocation = currentLocation;
      Message message = Message.obtain();
      message.what = 100;
      Bundle bundle = new Bundle();

      totalTime = 0L;
      bundle.putParcelable(GPSDataDTO.class.getName(), gpsDataDTO);
      startTime = location.getTime();
      message.setData(bundle);
      context.getRaceDataManager().setGpsData(new RaceGPSEvent(message));
    } else {

      int distanceBetweenLocationsInMeters = (int) currentLocation.distanceTo(prevLocation);

      BigDecimal timeBetweenLocationsInSeconds = new BigDecimal(
          currentLocation.getTime() - prevLocation.getTime());
      timeBetweenLocationsInSeconds = timeBetweenLocationsInSeconds
          .divide(SECOND_DIVIDER, 3, BigDecimal.ROUND_HALF_EVEN);

      BigDecimal distance = new BigDecimal(String.valueOf(distanceBetweenLocationsInMeters));
      if (timeBetweenLocationsInSeconds.compareTo(BigDecimal.ZERO) != 0) {
        speed = mpsToKph(
            distance.divide(timeBetweenLocationsInSeconds, 2, BigDecimal.ROUND_HALF_EVEN));
      }
      if (speed != null) {
        speed = speed.setScale(2, BigDecimal.ROUND_HALF_EVEN);
      }
      totalDistance = totalDistance + distanceBetweenLocationsInMeters;
      totalTime = currentLocation.getTime() - startTime;

      BigDecimal acceleration;
      if (timeBetweenLocationsInSeconds.multiply(timeBetweenLocationsInSeconds)
          .compareTo(BigDecimal.ZERO) != 0) {
        acceleration = distance
            .divide(timeBetweenLocationsInSeconds.multiply(timeBetweenLocationsInSeconds), 2,
                BigDecimal.ROUND_HALF_EVEN);
      } else {
        acceleration = BigDecimal.ZERO;
      }
      Message message = Message.obtain();
      message.what = 100;
      Bundle bundle = new Bundle();

      gpsDataDTO.setSpeed(speed.intValue());
      gpsDataDTO.setAcceleration(acceleration.intValue());
      gpsDataDTO.setCurrentTime(totalTime);
      gpsDataDTO.setDistance(totalDistance);
      bundle.putParcelable(GPSDataDTO.class.getName(), gpsDataDTO);
      message.setData(bundle);
      context.getRaceDataManager().setGpsData(new RaceGPSEvent(message));

      prevLocation = currentLocation;

      checkForFinish();
    }
  }

  private void checkForFinish() {

    switch (raceType) {
      case DRAG:
        if (totalDistance >= raceDistance) {
          Message message = Message.obtain();
          message.what = STOP_GPS;
          EventBus.getDefault().post(new RaceGPSEvent(message));
        }

        break;
      case TOP_SPEED:
        if (speed.intValue() >= raceFinishSpeed) {
          Message message = Message.obtain();
          message.what = STOP_GPS;
          EventBus.getDefault().post(new RaceGPSEvent(message));
        }
        break;
      case CANNONBALL:
        if (currentLocation.distanceTo(raceFinishLocation) <= FINISH_DISTANCE_GEOFENCE) {
          Message message = Message.obtain();
          message.what = STOP_GPS;
          EventBus.getDefault().post(new RaceGPSEvent(message));
        }

        break;

    }

  }


  private BigDecimal mpsToKph(BigDecimal speed) {
    return speed.multiply(TO_KPH);
  }


}
