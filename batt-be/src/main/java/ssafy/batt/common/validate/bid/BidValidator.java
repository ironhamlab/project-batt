package ssafy.batt.common.validate.bid;

import static ssafy.batt.common.exception.bid.BidErrorCode.BID_ALREADY_EXISTS;
import static ssafy.batt.common.exception.bid.BidErrorCode.BID_CANNOT_BID_OWN_TRANSFER;
import static ssafy.batt.common.exception.bid.BidErrorCode.BID_PRICE_TOO_LOW;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import ssafy.batt.common.exception.bid.BidException;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BidValidator {

  public static void validateBidPrice(long bidPrice, long currentHighestBid) {
    if (bidPrice <= currentHighestBid) {
      throw new BidException(BID_PRICE_TOO_LOW);
    }
  }

  public static void validateBidderIsNotTransferOwner(String bidderEmail, String transferOwnerEmail) {
    if (StringUtils.equals(bidderEmail, transferOwnerEmail)) {
      throw new BidException(BID_CANNOT_BID_OWN_TRANSFER);
    }
  }

  public static void validateBidAlreadyExists(String bidderEmail, String currentHighestBidderEmail) {
    if (StringUtils.equals(bidderEmail, currentHighestBidderEmail)) {
      throw new BidException(BID_ALREADY_EXISTS);
    }
  }
}