package ssafy.batt.common.exception.auth;

import ssafy.batt.common.exception.BattException;
import ssafy.batt.common.exception.ErrorCode;

public class AuthException extends BattException {

  public AuthException(ErrorCode errorCode) {
    super(errorCode);
  }
}
