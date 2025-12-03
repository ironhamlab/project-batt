package ssafy.batt.common.exception.booking;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ssafy.batt.common.exception.ErrorCode;

@RequiredArgsConstructor
public enum BookingErrorCode implements ErrorCode {

  // 400 BAD REQUEST
  BOOKING_NOT_FOUND(BAD_REQUEST, "BATT401"),
  BOOKING_TRANSFER_DUPLICATE_REGISTRATION(BAD_REQUEST, "BATT402");


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