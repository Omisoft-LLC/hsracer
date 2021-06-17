package com.omisoft.hsracer.features.race;

import static com.omisoft.hsracer.constants.Constants.CONSTANT_LATITUDE;
import static com.omisoft.hsracer.constants.Constants.CONSTANT_LONGITUDE;
import static com.omisoft.hsracer.constants.Constants.DISPLAYED_LOCATION;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import com.fasterxml.jackson.databind.JsonNode;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.constants.URLConstants;
import com.omisoft.hsracer.dto.CannonBallAddressInfoDTO;
import com.omisoft.hsracer.utils.AndroidUtils;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * Activity to choose destination address for cannonball race
 */
public class CannonballMapActivity extends BaseActivity {

  private static final MessageFormat QUERY_FORMAT = new MessageFormat(
      URLConstants.OSM_ADDRESS_SEARCH_PROVIDER);
  private static final double DEFAULT_LATITUDE = 48.1179352;
  private static final double DEFAULT_LONGITUDE = 9.5376117;

  private double latitude = DEFAULT_LATITUDE;
  private double longitude = DEFAULT_LONGITUDE;
  private String displayedNamed;

  @BindView(R.id.destinationAddress)
  EditText mDestinationAddress;
  @BindView(R.id.cannonballMap)
  MapView mMapView;

  private CannonBallAddressInfoDTO addressInfo;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cannonball_map);
    if (savedInstanceState == null) {
      addressInfo = new CannonBallAddressInfoDTO();
      initMap();
    } else {
      addressInfo = savedInstanceState.getParcelable(CannonBallAddressInfoDTO.class.getName());
      latitude = savedInstanceState.getDouble(CONSTANT_LATITUDE);
      longitude = savedInstanceState.getDouble(CONSTANT_LONGITUDE);
      displayedNamed = savedInstanceState.getString(DISPLAYED_LOCATION);
      if (Double.compare(latitude, DEFAULT_LATITUDE) == 0.0) {
        initMap();
      } else {
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapView.setUseDataConnection(true);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        setMarkerOnMap();
      }
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(CannonBallAddressInfoDTO.class.getName(), addressInfo);
    outState.putDouble(CONSTANT_LATITUDE, latitude);
    outState.putDouble(CONSTANT_LONGITUDE, longitude);
    outState.putString(DISPLAYED_LOCATION, displayedNamed);
    super.onSaveInstanceState(outState);
  }

  /**
   * Initializes the map view
   */
  private void initMap() {
    mMapView.setBuiltInZoomControls(true);
    mMapView.setMultiTouchControls(true);
    mMapView.setUseDataConnection(true);

    if (!AndroidUtils.isSDCardAvailable()) {
//      org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants
//          .setCachePath(getCacheDir().getAbsolutePath());
    }

    mMapView.setTileSource(TileSourceFactory.MAPNIK);
    IMapController mapController = mMapView.getController();
    mapController.setZoom(5);
    GeoPoint startPoint = new GeoPoint(latitude, longitude);
    mapController.setCenter(startPoint);
  }


  @OnEditorAction(R.id.destinationAddress)
  public boolean onDestAdressEditorActionListener(TextView v, int actionId, KeyEvent event) {
    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
      performSearch();
      return true;
    }
    return false;
  }

  /**
   * When a destination is specified in the field the user is navigated to the {@link
   * CreateRaceActivity } with the coordinates taken from the map
   */
  @OnClick(R.id.set_destination_btn)
  public void setDestinationButtonListener(View view) {
    Bundle resultBundle = new Bundle();
    resultBundle.putParcelable(CannonBallAddressInfoDTO.class.getName(), addressInfo);
    Intent intent = new Intent();
    intent.putExtras(resultBundle);
    setResult(RESULT_OK, intent);
    hideSoftKeyboard();
    finish();
  }

  /**
   * Perform actual search from all location on the world specified in the database.
   */
  private void performSearch() {
    hideSoftKeyboard();
    if (!getApp().isConnected()) {
      showWhiteToastMessage(R.string.no_internet_connection);
      return;
    }
    String searchQuery = mDestinationAddress.getText().toString();
    Request request = new Request.Builder().url(QUERY_FORMAT.format(new Object[]{searchQuery}))
        .build();
    getHttpClient().newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        Log.wtf(TAG, "Failure");
        Log.wtf(TAG, e);
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful() && response.body() != null) {
          InputStream stream = response.body().byteStream();
          com.fasterxml.jackson.databind.ObjectMapper objectMapper = getObjectMapper();
          Iterator<JsonNode> it = objectMapper.readTree(stream).iterator();
          if (it.hasNext()) {
            JsonNode tree = it.next();
            if (tree.hasNonNull(CONSTANT_LATITUDE)) {
              final double latitude = tree.get(CONSTANT_LATITUDE).asDouble();
              final double longitude = tree.get(CONSTANT_LONGITUDE).asDouble();
              final String displayName = tree.get(DISPLAYED_LOCATION).asText();
              CannonballMapActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  updateMap(latitude, longitude, displayName);
                }
              });
            } else {
              CannonballMapActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  showWhiteToastMessage(R.string.no_address_found);

                }
              });
            }
          } else {
            CannonballMapActivity.this.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                showWhiteToastMessage(R.string.no_address_found);

              }
            });
          }

        } else {
          CannonballMapActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              showWhiteToastMessage(R.string.no_osm_server);
            }
          });
        }
      }
    });
  }

  /**
   * Updates the map with the new point typed by the user in the search bar. After each point the
   * map overlays are cleared
   */
  private void updateMap(double latitude, double longitude, String displayName) {
    addressInfo = new CannonBallAddressInfoDTO(latitude, longitude, displayName);
    mDestinationAddress.setText(displayName);
    displayedNamed = displayName;
    this.latitude = latitude;
    this.longitude = longitude;
    mMapView.getOverlays().clear();
    setMarkerOnMap();
  }

  /**
   * Sets marker on the map
   */
  private void setMarkerOnMap() {
    Marker marker = new Marker(mMapView);
    GeoPoint geoPoint = new GeoPoint(latitude, longitude);
    marker.setPosition(geoPoint);
    marker.setTitle(getString(R.string.finish_location, displayedNamed));
    marker.setAnchor(Marker.ANCHOR_CENTER,
        Marker.ANCHOR_BOTTOM);
    mMapView.getOverlays().add(marker);
    IMapController mapController = mMapView.getController();
    mapController.setZoom(16);
    mapController.setCenter(geoPoint);
  }

  public void searchLocation(View view) {
    performSearch();
  }
}

