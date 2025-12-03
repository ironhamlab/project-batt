package ssafy.batt.common.exception.seat;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ssafy.batt.common.exception.ErrorCode;

@RequiredArgsConstructor
public enum SeatErrorCode implements ErrorCode {

  // 400 BAD REQUEST
  SEAT_NOT_FOUND(BAD_REQUEST, "BATT701"),
  
  // 409 CONFLICT
  SEAT_ALREADY_RESERVED(CONFLICT, "BATT702"),
  SEAT_ALREADY_HELD(CONFLICT, "BATT703");

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