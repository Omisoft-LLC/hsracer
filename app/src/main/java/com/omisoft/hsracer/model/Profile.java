package com.omisoft.hsracer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * ProfileActivity information
 * Created by dido on 20.04.17.
 */

@Entity(indices = {@Index(value = "rest_id", unique = true)})
public class Profile extends BaseEntity {

  private String email;
  private String password;

  private String alias;

  private String firstName;

  private String lastName;

  private Short age;

  private String city;

  private String country;

  @ColumnInfo(name = "rest_id")
  private Long restId;

  public Profile() {

  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Short getAge() {
    return age;
  }

  public void setAge(Short age) {
    this.age = age;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public Long getRestId() {
    return restId;
  }

  public void setRestId(Long restId) {
    this.restId = restId;
  }

  public Profile(Cursor cursor) {
    this.setId(cursor.getLong(cursor.getColumnIndex("_id")));
    this.setAge(cursor.getShort(cursor.getColumnIndex("age")));
    this.setCity(cursor.getString(cursor.getColumnIndex("city")));
    this.setCountry(cursor.getString(cursor.getColumnIndex("country")));
    this.setAlias(cursor.getString(cursor.getColumnIndex("alias")));
    this.setEmail(cursor.getString(cursor.getColumnIndex("email")));
    this.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
    this.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
    this.setRestId(Long.parseLong(cursor.getString(cursor.getColumnIndex("rest_id"))));
    this.setPassword(cursor.getString(cursor.getColumnIndex("password")));

  }


  public ContentValues toContentValues() {
    ContentValues contentValues = new ContentValues();
    contentValues.put("age", age);
    contentValues.put("city", city);
    contentValues.put("country", country);
    contentValues.put("alias", alias);
    contentValues.put("firstName", firstName);
    contentValues.put("lastName", lastName);
    contentValues.put("rest_id", restId);
    contentValues.put("password", password);
    contentValues.put("email", email);
    return contentValues;
  }

  @Override
  public String toString() {
    return "Profile{" +
        "email='" + email + '\'' +
        ", password='" + password + '\'' +
        ", alias='" + alias + '\'' +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", age=" + age +
        ", city='" + city + '\'' +
        ", country='" + country + '\'' +
        ", restId=" + restId +
        '}';
  }
}
