package ssafy.batt.common.exception.member;

import static org.springframework.http.HttpStatus.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ssafy.batt.common.exception.ErrorCode;

@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

  // 401 UNAUTHORIZED
  MEMBER_NOT_FOUND(UNAUTHORIZED, "BATT101"),
  MEMBER_NOT_AUTHORIZED(UNAUTHORIZED, "BATT102");


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
