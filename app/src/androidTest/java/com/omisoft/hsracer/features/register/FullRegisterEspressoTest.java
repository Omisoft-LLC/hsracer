package com.omisoft.hsracer.features.register;

import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.github.javafaker.Faker;
import com.omisoft.hsracer.R;
import com.omisoft.hsracer.features.BaseEspressoTest;
import com.omisoft.hsracer.features.SplashActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@android.support.test.filters.LargeTest
@RunWith(AndroidJUnit4.class)
public class FullRegisterEspressoTest extends BaseEspressoTest {

  @Rule
  public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(
      SplashActivity.class);

  @Test
  public void registerEspressoTest() {

    Faker faker = new Faker();
    String email = faker.internet().emailAddress();
    String firstName = faker.name().firstName();
    String lastName = faker.name().lastName();
    String nickName = faker.name().username();
    String password = faker.internet().password();

    performClickOnView(R.id.agree_disclaimer_button, click());
    systemSleep(1000);

    performClickOnView(R.id.register_btn, click());
    systemSleep(1000);

    performEditText(R.id.nickname, clearText(), typeText(nickName), closeSoftKeyboard());
    performEditText(R.id.first_name, clearText(), typeText(firstName), closeSoftKeyboard());
    performEditText(R.id.last_name, clearText(), typeText(lastName), closeSoftKeyboard());
    performEditText(R.id.email_register, clearText(), typeText(email), closeSoftKeyboard());
    performEditText(R.id.password_register, scrollTo(), clearText(), typeText(password),
        closeSoftKeyboard());
    performEditText(R.id.repeat_password_register, scrollTo(), clearText(), typeText(password),
        closeSoftKeyboard());
    performClickOnView(R.id.register, scrollTo(), click());
    systemSleep(3000);

    performEditText(R.id.email, clearText(), typeText(email), closeSoftKeyboard());
    performEditText(R.id.password, clearText(), typeText(password), closeSoftKeyboard());
    performClickOnView(R.id.login_btn, click());
  }

}
