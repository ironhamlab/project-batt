package ssafy.batt.common.exception.handler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ssafy.batt.common.exception.ErrorResponse;
import ssafy.batt.common.exception.BattException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BattException.class)
  protected ErrorResponse handleException(BattException e) {
    return ErrorResponse.of(e.getErrorCode());
  }


}