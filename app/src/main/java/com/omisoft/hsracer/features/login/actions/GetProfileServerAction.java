package com.omisoft.hsracer.features.login.actions;

import static com.omisoft.hsracer.constants.URLConstants.AUTHORIZATION_HEADER;
import static com.omisoft.hsracer.constants.URLConstants.GET_PROFILE;
import static com.omisoft.hsracer.constants.URLConstants.REST_URL;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.common.BaseApp;
import com.omisoft.hsracer.common.loader.BaseServerAction;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.dao.CarDAO;
import com.omisoft.hsracer.dto.CarDTO;
import com.omisoft.hsracer.dto.PublicProfileDTO;
import com.omisoft.hsracer.exceptions.ServerException;
import com.omisoft.hsracer.model.Car;
import com.omisoft.hsracer.model.FuelType;
import com.omisoft.hsracer.model.Profile;
import com.omisoft.hsracer.utils.Utils;
import com.yakivmospan.scytale.Store;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import okhttp3.Request;
import org.greenrobot.eventbus.EventBus;

/**
 * Return profile from server
 * Created by dido on 13.06.17.
 */

public class GetProfileServerAction extends BaseServerAction<PublicProfileDTO> implements
    Closeable {

  private final BaseApp baseApp;
  private final SharedPreferences prefs;
  private static final String TAG = GetProfileServerAction.class.getName();

  public GetProfileServerAction(BaseApp context) {
    super(context, PublicProfileDTO.class);
    this.baseApp = context;
    prefs = PreferenceManager.getDefaultSharedPreferences(baseApp);
  }

  @Override
  public void run() {
    Store store = new Store(Utils.getApp());
    Request request = new Request.Builder().url(REST_URL + GET_PROFILE)
        .addHeader(AUTHORIZATION_HEADER, baseApp.getAuthId()).build();

    PublicProfileDTO profileDTO = null;
    try {
      profileDTO = loadDataFromServer(request);
      Long restId = prefs.getLong(Constants.REST_ID, 0);
      String password = baseApp.getCrypto()
          .decrypt(prefs.getString(Constants.PASSWORD, ""),
              store.getAsymmetricKey(BuildConfig.APPLICATION_ID, null));

      Profile oldProfile = baseApp.getDB().profileDAO().findByRestId(restId);
      if (oldProfile != null && oldProfile.getId() != null) {
        baseApp.getDB().carDAO().deleteAll(oldProfile.getId());
        baseApp.getDB().profileDAO().delete(oldProfile);
      }

      Profile profile = new Profile();
      profile.setRestId(restId);
      profile.setEmail(profileDTO.getEmail());
      profile.setPassword(password);
      profile.setCountry(profileDTO.getCountry());
      profile.setCity(profileDTO.getCity());
      profile.setAge(profileDTO.getAge());
      profile.setAlias(profileDTO.getNickName());
      profile.setFirstName(profileDTO.getFirstName());
      profile.setLastName(profileDTO.getLastName());

      Long profileId = baseApp.getDB().profileDAO().create(profile);

      SharedPreferences.Editor editor = prefs.edit();
      editor.putString(Constants.NICK_NAME,
          TextUtils.isEmpty(profileDTO.getNickName()) ? profileDTO.getFirstName() + " " + profileDTO
              .getLastName() : profileDTO.getNickName());
      editor.putString(Constants.USER_EMAIL, profileDTO.getEmail());
      editor.putLong(Constants.ID, profileId);
      editor.apply();
      // TODO Implement sync of cars from server
      if (!profileDTO.getCars().isEmpty()) {
        Log.wtf(TAG, "run: " + profileDTO.getCars().toString());
        insertCarsInDB(profileId, profileDTO.getCars());
      }

    } catch (ServerException e) {
      EventBus.getDefault().post(e);

    }

  }

  private void insertCarsInDB(Long profileId, List<CarDTO> cars) {
    CarDAO carDAO = baseApp.getDB().carDAO();
    for (CarDTO carDTO : cars) {
      Car car = new Car();
      car.setAlias(carDTO.getAlias());
      car.setManufacturer(carDTO.getMake());
      car.setModel(carDTO.getModel());
      car.setHpr(carDTO.getHpr());
      if (carDTO.getYear() == null) {
        car.setYear(2000);
      } else {
        car.setYear(Integer.valueOf(carDTO.getYear()));
      }
      if (carDTO.getFuel() == null) {
        car.setFuel(FuelType.PETROL);
      } else {
        car.setFuel(carDTO.getFuel());
      }
      car.setEngineCylinders(carDTO.getEngineCylinders());
      car.setVolume(carDTO.getVolume());
      car.setRestId(carDTO.getRestId());
      car.setProfileId(profileId);
      car.setVehicleType(carDTO.getVehicleType());
      carDAO.create(car);
    }
  }


  @Override
  public void close() throws IOException {
  }
}
