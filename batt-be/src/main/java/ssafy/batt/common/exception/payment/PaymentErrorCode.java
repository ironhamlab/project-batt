package ssafy.batt.common.exception.payment;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.PAYMENT_REQUIRED;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ssafy.batt.common.exception.ErrorCode;

@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

  // 400 BAD REQUEST
  PAYMENT_REQUEST_INVALID(BAD_REQUEST, "BATT801"),
  PAYMENT_NOT_FOUND(BAD_REQUEST, "BATT804"),
  PAYMENT_ALREADY_CANCELLED(BAD_REQUEST, "BATT805"),
  CANCEL_AMOUNT_MISMATCH(BAD_REQUEST, "BATT809"),
  
  // 402 PAYMENT REQUIRED
  PAYMENT_FAILED(PAYMENT_REQUIRED, "BATT802"),
  PAYMENT_CONFIRMATION_FAILED(PAYMENT_REQUIRED, "BATT808");

  private final HttpStatus httpStatus;
  private final String code;

  @Override
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  @Override
  public String getCode() {
    return code;
  }
}