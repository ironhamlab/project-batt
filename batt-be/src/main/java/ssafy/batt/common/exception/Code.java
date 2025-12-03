package ssafy.batt.common.exception;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum Code {

  // SUCCESS
  OK(HttpStatus.OK, "success"),

  // Member & Kakao OAuth2
  ALREADY_EXIST_MEMBER(CONFLICT, "이미 존재하는 유저입니다."),
  KAKAO_ACCOUNT_NOT_FOUND(UNAUTHORIZED, "Kakao 사용자 정보를 가져오는데 실패했습니다."),
  KAKAO_ACCOUNT_NOT_REGISTERED(UNAUTHORIZED, "카카오 계정이 등록되어 있지 않습니다. 회원가입이 필요합니다."),
  NOT_EXIST_MEMBER(NOT_FOUND, "존재하지 않는 유저입니다."),

  // Temp Token
  INVALID_TEMP_TOKEN(UNAUTHORIZED, "유효하지 않은 임시 토큰입니다."),

  // JWT Token
  INVALID_ACCESS_TOKEN(UNAUTHORIZED, "유효하지 않은 ACCESS_JWT 서명입니다."),
  EXPIRED_ACCESS_TOKEN(UNAUTHORIZED, "만료된 ACCESS_JWT 토큰입니다."),
  INVALID_REFRESH_TOKEN(UNAUTHORIZED, "유효하지 않은 REFRESH_JWT 서명입니다."),
  EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "만료된 REFRESH_JWT 토큰입니다."),
  UNSUPPORTED_JWT_TOKEN(BAD_REQUEST, "지원되지 않는 JWT 토큰 형식입니다."),
  EMPTY_JWT_TOKEN(BAD_REQUEST, "토큰이 비어있거나 제공되지 않았습니다.");

  private final HttpStatus httpStatus;
  private final String code;

}