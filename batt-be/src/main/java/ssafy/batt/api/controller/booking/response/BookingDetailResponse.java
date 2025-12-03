package ssafy.batt.api.controller.booking.response;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.booking.BookingStatus;
import ssafy.batt.domain.booking.PaymentMethod;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.seat.Grade;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class BookingDetailResponse {

  private String posterImageUrl;
  private String title;
  private String venueName;
  private String orderId;
  private Long performanceScheduleId;

  private Instant bookingCreatedAt;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate performanceDate;

  @JsonFormat(pattern = "HH:mm")
  private LocalTime performanceTime;

  private Instant cancellationDeadline;
  private String memberName;
  private BookingStatus status;
  private PaymentMethod paymentMethod;
  private int totalAmount;
  private int seatCount;
  private List<SeatDetail> seatDetails;

  @Getter
  @AllArgsConstructor(access = PRIVATE)
  public static class SeatDetail {

    private Long seatId;
    private int price;
    private Grade grade;
    private String seatNumber;
    private String bookingNumber;
  }

  public static BookingDetailResponse of(Member member, Booking booking) {
    return new BookingDetailResponse(
        booking.getPerformanceSchedule().getPerformance().getPosterImageUrl(),
        booking.getPerformanceSchedule().getPerformance().getTitle(),
        booking.getPerformanceSchedule().getPerformance().getVenueName(),
        booking.getOrderId(),
        booking.getPerformanceSchedule().getId(),
        booking.getCreatedAt(),
        booking.getPerformanceSchedule().getPerformanceDate(),
        booking.getPerformanceSchedule().getPerformanceTime(),
        booking.getCancelledDate(),
        member.getName(),
        booking.getStatus(),
        booking.getPaymentMethod(),
        booking.getTotalAmount(),
        booking.getSeatCount(),
        booking.getBookingSeats().stream()
            .map(seat -> new SeatDetail(
                seat.getId(),
                seat.getPrice(),
                seat.getSeat().getGrade(),
                seat.getSeat().generateFormattingSeatNumber(),
                seat.getBookingNumber())
            ).toList()
    );
  }
}