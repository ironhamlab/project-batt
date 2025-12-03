package ssafy.batt.common.exception.coin;

import ssafy.batt.common.exception.BattException;
import ssafy.batt.common.exception.ErrorCode;

public class CoinException extends BattException {

  public CoinException(ErrorCode errorCode) {
    super(errorCode);
  }
}
