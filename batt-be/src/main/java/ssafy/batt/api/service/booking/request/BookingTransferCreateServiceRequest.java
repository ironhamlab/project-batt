package ssafy.batt.api.service.booking.request;

import java.time.Instant;
import ssafy.batt.api.controller.transfer.request.BookingTransferCreateRequest;

public record BookingTransferCreateServiceRequest(
    Instant transferEndDateTime
) {

  public static BookingTransferCreateServiceRequest from(BookingTransferCreateRequest request) {
    return new BookingTransferCreateServiceRequest(request.transferEndDateTime());
  }
}