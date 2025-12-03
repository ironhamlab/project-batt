package ssafy.batt.common.exception.member;

import ssafy.batt.common.exception.BattException;
import ssafy.batt.common.exception.ErrorCode;

public class MemberException extends BattException {

  public MemberException(ErrorCode errorCode) {
    super(errorCode);
  }
}
