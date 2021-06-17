package com.omisoft.hsracer.features.register;


import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.github.javafaker.Faker;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.features.BaseEspressoTest;
import com.omisoft.hsracer.features.login.LoginActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test to simulate user register with a new and already existing email
 * Created by developer on 20.07.17.
 */
@RunWith(AndroidJUnit4.class)
public class RegisterUserEspressoTest extends BaseEspressoTest {

  private String mFirstName;
  private String mLastName;
  private String mNickName;
  private String mPassword;
  private String mEmail;

  @Rule
  public IntentsTestRule<RegisterActivity> mActivityRule = new IntentsTestRule<>(
      RegisterActivity.class);

  @Before
  public void loadFakeData() {
    Faker faker = new Faker();
    this.mEmail = faker.internet().emailAddress();
    this.mFirstName = faker.name().firstName();
    this.mLastName = faker.name().lastName();
    this.mNickName = faker.name().username();
    this.mPassword = faker.internet().password();
  }


  @Test
  public void wrongEmail() {
    switchWifiState(getActivityInstance(), true);
    performEditText(R.id.nickname, clearText(), typeText(mNickName), closeSoftKeyboard());
    performEditText(R.id.first_name, clearText(), typeText(mFirstName), closeSoftKeyboard());
    performEditText(R.id.last_name, clearText(), typeText(mLastName), closeSoftKeyboard());
    performEditText(R.id.email_register, clearText(), typeText("bullshit23!!!!!"),
        closeSoftKeyboard());
    performEditText(R.id.password_register, scrollTo(), clearText(), typeText(mPassword),
        closeSoftKeyboard());
    performEditText(R.id.repeat_password_register, scrollTo(), clearText(), typeText(mPassword),
        closeSoftKeyboard());
    performClickOnView(R.id.register, scrollTo(), click());
    systemSleep(2000);
//    checkIfActivityIsTheSame(RegisterActivity.class);
    checkIfActivityIsTheSameAndIntentNotLaunched(LoginActivity.class.getName());
//    checkIfToastIsDisplayed(R.string.not_correct_email,mActivityRule.getActivity());
  }

  @Test
  public void existingEmail() {
    switchWifiState(getActivityInstance(), true);
    performEditText(R.id.nickname, clearText(), typeText(mNickName), closeSoftKeyboard());
    performEditText(R.id.first_name, clearText(), typeText(mFirstName), closeSoftKeyboard());
    performEditText(R.id.last_name, clearText(), typeText(mLastName), closeSoftKeyboard());
    performEditText(R.id.email_register, clearText(), typeText("yoricha@abv.bg"),
        closeSoftKeyboard());
    performEditText(R.id.password_register, scrollTo(), clearText(), typeText(mPassword),
        closeSoftKeyboard());
    performEditText(R.id.repeat_password_register, scrollTo(), clearText(), typeText(mPassword),
        closeSoftKeyboard());
    performClickOnView(R.id.register, scrollTo(), click());
    systemSleep(2000);
    checkIfActivityIsTheSameAndIntentNotLaunched(LoginActivity.class.getName());
  }

  @Test
  public void noEmail() {
    switchWifiState(getActivityInstance(), true);
    performEditText(R.id.nickname, clearText(), typeText(mNickName), closeSoftKeyboard());
    performEditText(R.id.first_name, clearText(), typeText(mFirstName), closeSoftKeyboard());
    performEditText(R.id.last_name, clearText(), typeText(mLastName), closeSoftKeyboard());
    performEditText(R.id.password_register, scrollTo(), clearText(), typeText(mPassword),
        closeSoftKeyboard());
    performEditText(R.id.repeat_password_register, scrollTo(), clearText(), typeText(mPassword),
        closeSoftKeyboard());
    performClickOnView(R.id.register, scrollTo(), click());
    systemSleep(2000);
    checkIfActivityIsTheSameAndIntentNotLaunched(LoginActivity.class.getName());

  }

  @Test
  public void noPasswordFirst() {
    switchWifiState(getActivityInstance(), true);
    performEditText(R.id.nickname, clearText(), typeText(mNickName), closeSoftKeyboard());
    performEditText(R.id.first_name, clearText(), typeText(mFirstName), closeSoftKeyboard());
    performEditText(R.id.last_name, clearText(), typeText(mLastName), closeSoftKeyboard());
    performEditText(R.id.email_register, clearText(), typeText(mEmail),
        closeSoftKeyboard());
    performEditText(R.id.password_register, scrollTo(), clearText(), typeText(""),
        closeSoftKeyboard());
    performEditText(R.id.repeat_password_register, scrollTo(), clearText(), typeText(mPassword),
        closeSoftKeyboard());
    performClickOnView(R.id.register, scrollTo(), click());
    systemSleep(2000);
    checkIfActivityIsTheSameAndIntentNotLaunched(LoginActivity.class.getName());
  }

  @Test
  public void noPasswordSecond() {
    switchWifiState(getActivityInstance(), true);
    performEditText(R.id.nickname, clearText(), typeText(mNickName), closeSoftKeyboard());
    performEditText(R.id.first_name, clearText(), typeText(mFirstName), closeSoftKeyboard());
    performEditText(R.id.last_name, clearText(), typeText(mLastName), closeSoftKeyboard());
    performEditText(R.id.email_register, clearText(), typeText(mEmail),
        closeSoftKeyboard());
    performEditText(R.id.password_register, scrollTo(), clearText(), typeText(mPassword),
        closeSoftKeyboard());
    performEditText(R.id.repeat_password_register, scrollTo(), clearText(), typeText(""),
        closeSoftKeyboard());
    performClickOnView(R.id.register, scrollTo(), click());
    systemSleep(2000);
    checkIfActivityIsTheSameAndIntentNotLaunched(LoginActivity.class.getName());
  }

  @Test
  public void noMatchPassword() {
    switchWifiState(getActivityInstance(), true);
    performEditText(R.id.nickname, clearText(), typeText(mNickName), closeSoftKeyboard());
    performEditText(R.id.first_name, clearText(), typeText(mFirstName), closeSoftKeyboard());
    performEditText(R.id.last_name, clearText(), typeText(mLastName), closeSoftKeyboard());
    performEditText(R.id.email_register, clearText(), typeText(mEmail),
        closeSoftKeyboard());
    performEditText(R.id.password_register, scrollTo(), clearText(), typeText(mPassword),
        closeSoftKeyboard());
    performEditText(R.id.repeat_password_register, scrollTo(), clearText(), typeText("asd"),
        closeSoftKeyboard());
    performClickOnView(R.id.register, scrollTo(), click());
    systemSleep(2000);
    checkIfActivityIsTheSameAndIntentNotLaunched(LoginActivity.class.getName());
  }

  @Test
  public void noInternetConnection() {
    switchWifiState(getActivityInstance(), false);
    performEditText(R.id.nickname, clearText(), typeText(mNickName), closeSoftKeyboard());
    performEditText(R.id.first_name, clearText(), typeText(mFirstName), closeSoftKeyboard());
    performEditText(R.id.last_name, clearText(), typeText(mLastName), closeSoftKeyboard());
    performEditText(R.id.email_register, clearText(), typeText(mEmail),
        closeSoftKeyboard());
    performEditText(R.id.password_register, scrollTo(), clearText(), typeText(mPassword),
        closeSoftKeyboard());
    performEditText(R.id.repeat_password_register, scrollTo(), clearText(), typeText(mPassword),
        closeSoftKeyboard());
    performClickOnView(R.id.register, scrollTo(), click());
    systemSleep(2000);
    checkIfActivityIsTheSameAndIntentNotLaunched(LoginActivity.class.getName());
  }

}
