package com.omisoft.hsracer.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.omisoft.hsracer.constants.Severity;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * ErrorDTO, transfers error messages to clients
 * Created by leozhekov on 11/1/16.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorDTO implements Parcelable {

  public static final Creator<ErrorDTO> CREATOR = new Creator<ErrorDTO>() {
    @Override
    public ErrorDTO createFromParcel(Parcel in) {
      return new ErrorDTO(in);
    }

    @Override
    public ErrorDTO[] newArray(int size) {
      return new ErrorDTO[size];
    }
  };
  private String detailedMessage;
  private Severity severity;
  private int errorCode;
  private String err;
  private Map<String, String> validationMessages = new HashMap<>();

  public ErrorDTO() {

  }

  /**
   * Constructs new ErrorDto.
   *
   * @param detailedMessage error title
   */
  public ErrorDTO(String err, String detailedMessage) {
    this.err = err;
    this.detailedMessage = detailedMessage;
    // this.severity = Severity.ERROR;

  }

  public ErrorDTO(String err, Throwable exception) {
    this.err = err;
    this.detailedMessage = exception.getMessage();
  }

  public ErrorDTO(String err, String detailedMessage, Map<String, String> validationMessages) {
    this.err = err;
    this.detailedMessage = detailedMessage;
    this.validationMessages = validationMessages;
  }

  /**
   * Constructs new error dto and set severity.
   *
   * @param detailedMessage error title
   * @param severity severity
   */
  public ErrorDTO(String err, String detailedMessage, Severity severity) {
    this.err = err;
    this.detailedMessage = detailedMessage;
    this.severity = severity;
  }

  public ErrorDTO(String err, String detailedMessage, Severity severity, int errorCode) {
    this.err = err;
    this.detailedMessage = detailedMessage;
    this.severity = severity;
    this.errorCode = errorCode;
  }

  protected ErrorDTO(Parcel in) {
    detailedMessage = in.readString();
    severity = Severity.valueOf(in.readString());
    errorCode = in.readInt();
    err = in.readString();
    int size = in.readInt();
    for (int i = 0; i < size; i++) {
      String key = in.readString();
      String value = in.readString();
      validationMessages.put(key, value);
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(detailedMessage);
    dest.writeString(severity.name());
    dest.writeInt(errorCode);
    dest.writeString(err);
    dest.writeInt(validationMessages.size());
    for (Map.Entry<String, String> entry : validationMessages.entrySet()) {
      dest.writeString(entry.getKey());
      dest.writeString(entry.getValue());
    }
  }
}
