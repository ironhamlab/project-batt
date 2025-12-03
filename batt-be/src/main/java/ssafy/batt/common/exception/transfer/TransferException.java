package ssafy.batt.common.exception.transfer;

import ssafy.batt.common.exception.BattException;
import ssafy.batt.common.exception.ErrorCode;

public class TransferException extends BattException {

  public TransferException(ErrorCode errorCode) {
    super(errorCode);
  }
}
