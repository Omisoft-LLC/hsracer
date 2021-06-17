package com.omisoft.hsracer.features.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import com.omisoft.hsracer.BuildConfig;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.common.BaseActivity;
import com.omisoft.hsracer.constants.Constants;
import com.omisoft.hsracer.constants.FirebaseEvent;
import com.omisoft.hsracer.dto.ProfileDTO;
import com.omisoft.hsracer.features.profile.actions.GetPersonalInformationDbAction;
import com.omisoft.hsracer.features.profile.actions.UpdatePersonalInformationServerAction;
import com.omisoft.hsracer.features.profile.actions.UpdateProfileDbAction;
import com.omisoft.hsracer.features.profile.events.FailedToUpdateProfileEvent;
import com.omisoft.hsracer.features.profile.events.SuccessGetProfileDbEvent;
import com.omisoft.hsracer.features.profile.events.SuccessUpdateProfileEvent;
import com.omisoft.hsracer.model.Profile;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Profile Info activity
 * Created by Omisoft LLC. on 4/24/17.
 */

public class ProfileActivity extends BaseActivity {

  private static final String PROFILE_ID_INSTANCE_CONST = "profileId";
  private static final int GET_COUNTRIES_LOADER = 3;

  @BindView(R.id.alias)
  EditText aliasEditText;
  @BindView(R.id.first_name)
  EditText firstNameEditText;
  @BindView(R.id.last_name)
  EditText lastNameEditText;
  @BindView(R.id.age)
  EditText ageEditText;
  @BindView(R.id.city)
  EditText cityEditText;
  @BindView(R.id.countries_autocomplete)
  AutoCompleteTextView countryAutoCompleteTextView;
  @BindView(R.id.updatePersonalInformationBtn)
  Button updatePersonalInfoButton;

  private Long profileId;
  private Long restId;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_personal_information);
    init();
  }

  @Override
  public void onStop() {
    EventBus.getDefault().removeStickyEvent(SuccessGetProfileDbEvent.class);
    super.onStop();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }



  /**
   * Makes request to the server and takes the info about th user.
   */
  private void init() {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    profileId = sharedPreferences.getLong(Constants.ID, 0);
    getExecutor().submit(new GetPersonalInformationDbAction(getApp(), profileId));
  }

  /**
   * When the user specifies his personal info and the update button is clicked is send request to
   * the server and the DB updating the info.
   */
  public void updatePersonalInformationButtonClickListener(View view) {
    if (getApp().isConnected()) {
      if (ageEditText.getText().toString().length() >= 2) {
        String alias = aliasEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String age = ageEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String country = countryAutoCompleteTextView.getText().toString();
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setAlias(alias);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        Short userAge = new Short(age);
        profile.setAge(userAge);
        profile.setCity(city);
        profile.setCountry(country);
        profile.setRestId(restId);
        getExecutor().submit(new UpdateProfileDbAction(getApp(), profile));
//TODO: Separate the logic about saving the profile info to the server and the local DB
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setAge(userAge);
        profileDTO.setNickName(alias);
        profileDTO.setFirstName(firstName);
        profileDTO.setLastName(lastName);
        profileDTO.setCity(city);
        profileDTO.setCountry(country);
        getExecutor()
            .submit(new UpdatePersonalInformationServerAction(getApp(), profileDTO));
        if (!BuildConfig.DEBUG) {
          Bundle firebaseParamBundle = new Bundle();
          mFirebaseAnalytics.logEvent(FirebaseEvent.PROFILE_UPDATE, firebaseParamBundle);
        }
      } else {
        showWhiteToastMessage(getString(R.string.age_number_too_high));
      }
    } else {
      showWhiteToastMessage(getString(R.string.no_internet_connection));
    }
  }

  /**
   * Here is initialized the autocomplete editText in which will be populated the countries
   */
  private void autoCompleteTextViews(List<String> countriesLiest) {
    ArrayAdapter<String> countriesAdapter = new ArrayAdapter<>(this, R.layout.spinner_row,
        R.id.text_view_row, countriesLiest);
    AutoCompleteTextView countries = findViewById(
        R.id.countries_autocomplete);
    countries.setThreshold(1);
    countries.setAdapter(countriesAdapter);
  }

  /**
   * This method sets the information about the profile in the fields. Comes from {@link
   * GetPersonalInformationDbAction}
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
  public void profileTakenHandler(SuccessGetProfileDbEvent event) {
    Profile profile = event.getProfile();
    aliasEditText.setText(profile.getAlias());
    firstNameEditText.setText(profile.getFirstName());
    lastNameEditText.setText(profile.getLastName());
    if (profile.getAge() != null) {
      ageEditText.setText(String.valueOf(profile.getAge()));
    }
    cityEditText.setText(profile.getCity());
    countryAutoCompleteTextView.setText(profile.getCountry());
    restId = profile.getRestId();
    autoCompleteTextViews(event.getCountries());
  }

  /**
   * Called if the uploading of the profile fail on the server.Send by {@link
   * FailedToUpdateProfileEvent}
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void failedToUpdateProfile(FailedToUpdateProfileEvent event) {
    showWhiteToastMessage(event.getMessage());
  }

  /**
   * After success on updating the personal info from the server this method is called from {@link
   * UpdatePersonalInformationServerAction}
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void profileUpdatedHandler(SuccessUpdateProfileEvent event) {
    finish();
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {

    savedInstanceState.putLong(PROFILE_ID_INSTANCE_CONST, profileId);

    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    profileId = savedInstanceState.getLong(PROFILE_ID_INSTANCE_CONST, profileId);
  }
}

