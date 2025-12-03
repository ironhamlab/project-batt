package ssafy.batt.common.exception;

import lombok.Getter;

@Getter
public class BattException extends RuntimeException {

  private final ErrorCode errorCode;

  public BattException(ErrorCode errorCode) {
    super(errorCode.getCode());
    this.errorCode = errorCode;
  }
}