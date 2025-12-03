package ssafy.batt.common.exception.coin;

import static org.springframework.http.HttpStatus.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ssafy.batt.common.exception.ErrorCode;

@RequiredArgsConstructor
public enum CoinErrorCode implements ErrorCode {

  COIN_INSUFFICIENT(BAD_REQUEST, "BATT201"),
  COIN_TRANSACTION_NOT_SUPPORTED(BAD_REQUEST, "BATT202");

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
