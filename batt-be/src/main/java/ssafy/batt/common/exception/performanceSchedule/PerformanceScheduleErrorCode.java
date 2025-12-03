package ssafy.batt.common.exception.performanceSchedule;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ssafy.batt.common.exception.ErrorCode;

@RequiredArgsConstructor
public enum PerformanceScheduleErrorCode implements ErrorCode {

  // 400 BAD REQUEST
  PERFORMANCE_SCHEDULE_NOT_FOUND(BAD_REQUEST, "BATT301"),
  SAME_DAY_CANCEL_NOT_ALLOWED(BAD_REQUEST, "BATT302");

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