package ssafy.batt.api.service.booking.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.booking.BookingStatus;

@Getter
@JsonInclude(NON_NULL)
public class BookingResponse {

  private Long bookingId;

  private String title;

  private List<String> bookingNumbers;

  private String orderId;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate performanceDate;

  @JsonFormat(pattern = "HH:mm")
  private LocalTime performanceTime;

  private Integer seatCount;

  private String venueName;

  private BookingStatus status;

  private Instant bookingCreatedAt;

  @Builder
  private BookingResponse(Long bookingId, String title, List<String> bookingNumbers, String orderId,
      LocalDate performanceDate, LocalTime performanceTime, Integer seatCount, String venueName, BookingStatus status,
      Instant bookingCreatedAt) {
    this.bookingId = bookingId;
    this.title = title;
    this.bookingNumbers = bookingNumbers;
    this.orderId = orderId;
    this.performanceDate = performanceDate;
    this.performanceTime = performanceTime;
    this.seatCount = seatCount;
    this.venueName = venueName;
    this.status = status;
    this.bookingCreatedAt = bookingCreatedAt;
  }

  public static BookingResponse of(
      Booking booking,
      List<String> bookingNumbers,
      Instant bookingCreatedAt
  ) {
    return BookingResponse.builder().
        bookingId(booking.getId())
        .title(booking.getPerformanceSchedule().getPerformance().getTitle())
        .bookingNumbers(bookingNumbers)
        .orderId(booking.getOrderId())
        .performanceDate(booking.getPerformanceSchedule().getPerformanceDate())
        .performanceTime(booking.getPerformanceSchedule().getPerformanceTime())
        .seatCount(booking.getSeatCount())
        .venueName(booking.getPerformanceSchedule().getPerformance().getVenueName())
        .status(booking.getStatus())
        .bookingCreatedAt(bookingCreatedAt)
        .build();
  }
}