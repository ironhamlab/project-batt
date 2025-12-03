package ssafy.batt.common.exception.transfer;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ssafy.batt.common.exception.ErrorCode;

@RequiredArgsConstructor
public enum TransferErrorCode implements ErrorCode {

  // 400 BAD REQUEST
  TRANSFER_NOT_FOUND(BAD_REQUEST, "BATT501"),
  TRANSFER_ALREADY_CLOSED(BAD_REQUEST, "BATT502"),
  TRANSFER_COMPLETION_ERROR(BAD_REQUEST, "BATT503"),
  TRANSFER_END_TIME_MUST_BE_FUTURE(BAD_REQUEST, "BATT504"),
  TRANSFER_END_TIME_MUST_BE_BEFORE_PERFORMANCE(BAD_REQUEST, "BATT505");

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
