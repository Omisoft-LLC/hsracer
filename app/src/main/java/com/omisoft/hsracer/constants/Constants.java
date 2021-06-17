package com.omisoft.hsracer.constants;

import okhttp3.MediaType;

/**
 * General constants
 * Created by Omisoft LLC. on 4/21/17.
 */

public class Constants {

  public static final String NO_STREAM_FILE = "no_stream.mp4";

  private Constants() {

  }

  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  public static final String EMAIL_REGEX =
      "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
  public static final String USER_EMAIL = "USER_EMAIL";

  //Shared preferences keys
  public static final String SHOWED_DISC = "SHOWED_DISC";
  // Current user rest id (long)
  public static final String REST_ID = "REST_ID";
  public static final String ID = "id";
  public static final String DEFAULT_CAR_ID = "DEFAULT_CAR_ID";
  public static final String EMAIL = "EMAIL";
  public static final String PASSWORD = "PASSWORD";
  public static final String CAR_REST_ID = "carRestId";
  public static final String AUTH_ID = "AUTH_ID";
  public static final String CREATOR = "creator";
  // Current race_id (long)
  public static final String RACE_ID = "raceId";
  public static final String RACE_IS_RUNNING = "RACE_IS_RUNNING";
  public static final String RECORD_VIDEO = "recordVideo";
  public static final String SHOW_PREVIEW = "showPreview";
  public static final String VIDEO_POSITION_FROM_GALLERY = "video_position_from_gallery";
  public static final String VIDEO_POSITION_FROM_APP = "video_position_from_app";
  public static final String LIVE_OBD_DATA = "liveOBDData";
  public static final String VIDEO_FILE_EXTENSION = ".mp4";
  public static final String SYSTEM_ALERT_WINDOW_PERMISSION = "systemAlertWindow";
  //Intent extra keys
  public static final String LOCAL_CAR_ID = "localCarId";
  public static final String SELECTED_CAR = "selected_car";

  //BuddyFinder constants
  public static final int JOINED_FINDER = 5;
  public static final int RECEIVED_MESSAGE = 12;
  public static final int RECEIVER_RACERS_PULL = 15;

  //HomeActivity constants
  public static final String CARS_LIST = "cars";
  public static final int RACER_CREATES_RACE = 1;
  public static final int RACER_JOINS_RACE = 2;

  public static final String DEFAULT_NICK_NAME = "Mystery racer";
  public static final String NULL_NICK_NAME = "null";

  //BuddyFinder constants
  public static final String NICK_NAME = "alias";
  public static final String FIREBASE_TOKEN = "FIREBASE_TOKEN";

  //OBD constants
  public static final String OBD_ENABLE_KEY = "obd-enable-key";
  public static final String OBD_DEVICE_KEY = "obd-devices-key"; /* mac address of obd adapter */
  public static final String TEMPERATURE_SCALE_KEY = "temperature-scale-key";

  //Mock Location constants
  public static final String MOCK_LOCATION = "mockLocation";

  //Memory availability constants
  public static final int MINIMUM_MEMORY_MB = 50;

  //Resolution constants
  public static final String RESOLUTION_KEY = "resolution-key";
  public static final String NEARBY_DATA_SEPARATOR = ":";

  //Video upload constants
  public static final String KEY_RECYCLER_VIDEOS = "recycler_video_list";
  public static final String LAST_KNOWN_VIDEO_PATH = "last_known_known_video";
  public static final String VIDEO_POSITION = "video_position";
  public static final String VIDEO_CURRENT_INDEX = "current_playing_video";
  public static final String VIDEOS_FOR_UPLOADING = "videos_for_uploading";
  public static final String VIDEOS_FOR_UPLOADING_BUNDLE = "videos_for_uploading_bundle";
  public static final String VIDEO_PREFERENCES = "videos";
  public static final String NUMBER_OF_VIDEOS = "number_of_videos";
  public static final String CURRENT_VIDEO_FOR_UPLOADING = "current_video";

  //Service Upload video constants
  public static final int TIME_REPEATING = 60 * 1000;
  public static final int TRIGGER_REPEATING = 10 * 60 * 1000;

  //Time constants
  public static final int ONE_SECOND = 1000;
  public static final int ONE_MINUTE = ONE_SECOND * 60;
  public static final int ONE_HOUR = ONE_MINUTE * 60;

  //Results constants
  public static final String LIST_WITH_RESULTS = "results_list";
  public static final String NUMBER_OF_PAGES = "pages";
  public static final String NUMBER_OF_ALL_PAGES = "all_pages";

  //fragment tag
  public static final String FRAGMENT_TAG = "fragment_tag";

  //Api key for uploading videos
  public static final String API_KEY = "api_key";

  //create race
  public static final String RACE_NAME = "race_name";

  //Results
  public static final String NAME_OF_RACE = "name";
  public static final String FINISH_POSITION = "position";
  public static final String RACE_MAX_SPEED = "maxSpeed";
  public static final String RACE_SHARE_URL = "url";
  public static final String RACE_DATE = "date";
  public static final String RACE_TYPE = "type";
  public static final String RACE_TIME = "time";
  public static final String RACE_DISTANCE = "distance";
  public static final String RACE_DESCRIPTION = "description";

  //cannonball race constants
  public static final String CONSTANT_LATITUDE = "lat";
  public static final String CONSTANT_LONGITUDE = "lon";
  public static final String DISPLAYED_LOCATION = "display_name";
  public final static int ITEMS_PER_PAGE = 20;
  //UpdateCarInfoFragment bundle arguments

  public static final String CAR_EDIT = "car_edit";

  public static final String LIVE_STREAM = "live_stream_video";
  public static String AES_KEY = "aes_key";
}
