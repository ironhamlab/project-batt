package ssafy.batt.common.exception.review;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ssafy.batt.common.exception.ErrorCode;

@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

  // 400 BAD REQUEST
  REVIEW_NOT_FOUND(BAD_REQUEST, "BATT301"),
  REVIEW_ALREADY_EXISTS(BAD_REQUEST, "BATT302");

  private final HttpStatus httpStatus;
  private final String code;

  @Override
  public HttpStatus getHttpStatus() {
    return null;
  }

  @Override
  public String getCode() {
    return "";
  }
}
