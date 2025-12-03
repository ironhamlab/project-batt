package ssafy.batt.common.exception.review;

import ssafy.batt.common.exception.BattException;
import ssafy.batt.common.exception.ErrorCode;

public class ReviewException extends BattException {

  public ReviewException(ErrorCode errorCode) {
    super(errorCode);
  }
}
