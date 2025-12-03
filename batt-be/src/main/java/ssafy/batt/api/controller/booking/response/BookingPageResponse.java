package ssafy.batt.api.controller.booking.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import ssafy.batt.api.service.booking.response.BookingResponse;

@Getter
@JsonInclude(NON_NULL)
@AllArgsConstructor(access = PRIVATE)
public class BookingPageResponse {

  private final PageInfo pageInfo;
  private final List<BookingResponse> bookings;

  @Getter
  @AllArgsConstructor(access = PRIVATE)
  public static class PageInfo {

    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
  }

  public static BookingPageResponse from(Page<BookingResponse> bookingResponse) {

    PageInfo pageInfo = new PageInfo(
        bookingResponse.getTotalElements(),
        bookingResponse.getTotalPages(),
        bookingResponse.getNumber() + 1,
        bookingResponse.getSize()
    );

    List<BookingResponse> bookings = bookingResponse.getContent();
    return new BookingPageResponse(pageInfo, bookings);
  }
}