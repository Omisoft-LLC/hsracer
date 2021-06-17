package com.omisoft.hsracer;

import static org.junit.Assert.assertEquals;

import com.github.javafaker.Faker;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@Deprecated
public class ExampleUnitTest {

  @Test
  public void addition_isCorrect() throws Exception {
    assertEquals(4, 2 + 2);
    Faker faker = new Faker();

  }
}