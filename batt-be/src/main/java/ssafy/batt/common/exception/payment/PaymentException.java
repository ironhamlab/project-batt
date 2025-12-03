package ssafy.batt.common.exception.payment;

import ssafy.batt.common.exception.BattException;
import ssafy.batt.common.exception.ErrorCode;

public class PaymentException extends BattException {

  public PaymentException(ErrorCode errorCode) {
    super(errorCode);
  }
}