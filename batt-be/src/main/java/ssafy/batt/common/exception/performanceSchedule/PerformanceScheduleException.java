package ssafy.batt.common.exception.performanceSchedule;

import ssafy.batt.common.exception.BattException;

public class PerformanceScheduleException extends BattException {

  public PerformanceScheduleException(PerformanceScheduleErrorCode errorCode) {
    super(errorCode);
  }
}