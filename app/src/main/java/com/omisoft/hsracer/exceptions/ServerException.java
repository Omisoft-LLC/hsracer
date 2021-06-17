package com.omisoft.hsracer.exceptions;

import com.omisoft.hsracer.dto.ErrorDTO;
import lombok.Getter;

/**
 * Web Server error
 * Created by dido on 10.06.17.
 */
@Getter
public class ServerException extends Exception {

  private final ErrorDTO errorDTO;

  public ServerException(ErrorDTO errorDTO) {
    this.errorDTO = errorDTO;
  }

  public ServerException(String code, String message) {
    errorDTO = new ErrorDTO(code, message);
  }

  public ServerException(int code, String message) {
    errorDTO = new ErrorDTO(String.valueOf(code), message);
  }

  /**
   * Copy constructor
   */
  public ServerException(ServerException e) {
    errorDTO = e.getErrorDTO();
  }

  public ServerException(Exception e) {

    errorDTO = new ErrorDTO(String.valueOf(500), e.getMessage());
  }
}
