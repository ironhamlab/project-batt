package ssafy.batt.api.controller.booking.response;

import java.util.List;
import ssafy.batt.api.service.booking.response.BookingResponse;

public record BookingListResponse(
    List<BookingResponse> bookings
) {

  public static BookingListResponse from(List<BookingResponse> bookingResponses) {
    return new BookingListResponse(bookingResponses);
  }
}