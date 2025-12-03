package ssafy.batt.common.exception.booking;

import ssafy.batt.common.exception.BattException;
import ssafy.batt.common.exception.ErrorCode;

public class BookingException extends BattException {

  public BookingException(ErrorCode errorCode) {
    super(errorCode);
  }
}