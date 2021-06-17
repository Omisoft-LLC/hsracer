package com.omisoft.hsracer.features.profile;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import com.github.javafaker.Faker;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.features.BaseEspressoTest;
import com.omisoft.hsracer.features.login.LoginActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/** Test in which is performed testing on profile updating
 * Created by developer on 25.10.17.
 */

@LargeTest
public class ChangeProfileInfo extends BaseEspressoTest {

  private String mFirstName;
  private String mLastName;
  private String mNickName;
  private String mCity;
  private String mCountry;
  private Integer mAge;
  @Rule
  public IntentsTestRule<LoginActivity> mActivityRule =
      new IntentsTestRule<>(LoginActivity.class);

  @Before
  public void fakeDataForPersonalInfo() {
    Faker faker = new Faker();
    this.mFirstName = faker.name().firstName();
    this.mLastName = faker.name().lastName();
    this.mNickName = faker.name().username();
    this.mCity = faker.address().city();
    this.mCountry = faker.address().country();
    this.mAge = faker.number().numberBetween(18, 80);
  }

  @Test
  public void changeProfileInfo() {
    performEditText(R.id.email, clearText(), typeText("dev1@abv.bg"), closeSoftKeyboard());
    performEditText(R.id.password, clearText(), typeText("asd"), closeSoftKeyboard());
    performClickOnView(R.id.login_btn, click());
    systemSleep(1000);
    onView(withId(R.id.drawer_layout)).perform(open());
    onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));
    systemSleep(1000);
    performEditText(R.id.alias, clearText(), typeText(mNickName), closeSoftKeyboard());
    performEditText(R.id.first_name, clearText(), typeText(mFirstName), closeSoftKeyboard());
    performEditText(R.id.last_name, clearText(), typeText(mLastName), closeSoftKeyboard());
    performEditText(R.id.age, scrollTo(), clearText(), typeText(String.valueOf(mAge)),
        closeSoftKeyboard());
    performEditText(R.id.city, scrollTo(), clearText(), typeText(mCity), closeSoftKeyboard());
    performEditText(R.id.countries_autocomplete, scrollTo(), clearText(), typeText(mCountry),
        closeSoftKeyboard());
    performClickOnView(R.id.updatePersonalInformationBtn, scrollTo(), click());
    checkIfActivityIsTheSame(ProfileActivity.class);

  }


}
