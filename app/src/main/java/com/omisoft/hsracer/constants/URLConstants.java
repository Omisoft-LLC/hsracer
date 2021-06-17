package com.omisoft.hsracer.constants;

import com.omisoft.hsracer.BuildConfig;

/**
 * URL Constants
 * Created by Omisoft LLC. on 4/21/17.
 */

public class URLConstants {

  public static final String[] STUN_SERVERS_ = {"stun.l.google.com:19302", "stun.ekiga.net",
      "stun.sipgate.net", "stun.sipgate.net"};

  public static final String TERMS_PAGE = "http://help.hsracer.com/terms.html";
  public static final String HELP_URL = "http://help.hsracer.com";
  public static final String WEBSITE_URL = "https://hsracer.com";

  public static final String REST_URL = BuildConfig.REST_URL;
  public static final String WS_URL = BuildConfig.WS_URL;
  public static final String AUTHORIZATION_HEADER = "authorization";
  public static final String KEY = "k";

  public static final String LOGIN_EP = "/account/login";
  public static final String RACE_WEB_SOCKET = WS_URL + "/race";
  public static final String CREATE_EP = "/account/create";
  public static final String GET_PROFILE = "/secure/profile/view";
  public static final String UPDATE_PROFILE = "/secure/profile";
  public static final String GET_RACES = "/secure/profile/race/result";
  public static final String GET_STAT_POS = "/secure/profile/stats/positions";
  public static final String ADD_CAR = "/secure/cars";
  public static final String UPDATE_CAR = "/secure/cars/";
  public static final String DELETE_CAR = "/secure/cars/delete/";
  public static final String REPORT_ERROR = "/reportError";
  public static final String PUBLISH = "/secure/publish";
  public static final String API_PUBLISH = "/secure_api/publish";
  public static final String UPLOAD = "/upload";
  public static final String SIGNAL_ENDPOINT = "/signal/";
  public static final String STREAMS = "/public/stream";
  public static final String VIEW = "/view";

  // See http://wiki.openstreetmap.org/wiki/Nominatim for query details
  public static final String OSM_ADDRESS_SEARCH_PROVIDER = "http://nominatim.openstreetmap.org/search?q={0}&format=json&limit=1&email=android@omisoft.eu";

  //BuddyFinder constant
  public static final String BUDDY_FINDER_RACE_WEB_SOCKET = WS_URL + "/find";

  public static final String URL_SHORTHENER_API = "https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyCsY6NXhXvJq6VYoznQZC7xfHS858yzKOo";

  public static final String HEADER_ANDROID_PACKAGE = "X-Android-Package";
  public static final String HEADER_ANDROID_CERT = "X-Android-Cert";
}