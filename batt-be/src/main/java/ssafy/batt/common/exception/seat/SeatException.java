package ssafy.batt.common.exception.seat;

import ssafy.batt.common.exception.BattException;
import ssafy.batt.common.exception.ErrorCode;

public class SeatException extends BattException {

  public SeatException(ErrorCode errorCode) {
    super(errorCode);
  }
}