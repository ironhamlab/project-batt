package ssafy.batt.common.exception.bid;

import static org.springframework.http.HttpStatus.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ssafy.batt.common.exception.ErrorCode;

@RequiredArgsConstructor
public enum BidErrorCode implements ErrorCode {

  // 400 BAD REQUEST
  BID_NOT_FOUND(BAD_REQUEST, "BATT601"),
  BID_PRICE_TOO_LOW(BAD_REQUEST, "BATT602"),
  BID_ALREADY_EXISTS(BAD_REQUEST, "BATT603"),
  BID_CANNOT_BID_OWN_TRANSFER(BAD_REQUEST, "BATT604");

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