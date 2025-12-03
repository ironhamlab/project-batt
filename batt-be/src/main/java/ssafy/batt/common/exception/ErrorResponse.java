package ssafy.batt.common.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorResponse {

  private final HttpStatus httpStatus;
  private final String code;

  public static ErrorResponse of(ErrorCode errorCode) {
    return new ErrorResponseBuilder()
        .httpStatus(errorCode.getHttpStatus())
        .code(errorCode.getCode())
        .build();
  }
}