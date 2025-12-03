package ssafy.batt.common.exception.bid;

import ssafy.batt.common.exception.BattException;
import ssafy.batt.common.exception.ErrorCode;

public class BidException extends BattException {

  public BidException(ErrorCode errorCode) {
    super(errorCode);
  }
}