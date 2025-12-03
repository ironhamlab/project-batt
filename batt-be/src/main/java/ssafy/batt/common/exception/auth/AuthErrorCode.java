package ssafy.batt.common.exception.auth;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ssafy.batt.common.exception.ErrorCode;

@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

  // Social Login -> 400
  UNSUPPORTED_PROVIDER(BAD_REQUEST, "BATT001"),
  LOGIN_FAILED(BAD_REQUEST, "BATT002"),

  // Token -> 401
  TOKEN_INVALID(UNAUTHORIZED, "BATT003"),
  TOKEN_EXPIRED(UNAUTHORIZED, "BATT004"),
  TOKEN_UNSUPPORTED(UNAUTHORIZED, "BATT005"),
  TOKEN_WRONG(UNAUTHORIZED, "BATT006"),
  TOKEN_NOT_MATCHED(UNAUTHORIZED, "BATT007"),
  REFRESH_TOKEN_NOT_EXIST_IN_COOKIE(UNAUTHORIZED, "BATT009"),
  REFRESH_TOKEN_EXPIRED(UNAUTHORIZED, "BATT009");

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