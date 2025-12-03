package ssafy.batt.domain.booking;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.Instant.now;
import static lombok.AccessLevel.PROTECTED;
import static ssafy.batt.common.snowflake.Snowflake.formattingReservationId;
import static ssafy.batt.domain.booking.BookingStatus.CONFIRMED;
import static ssafy.batt.domain.booking.BookingStatus.PENDING;
import static ssafy.batt.domain.booking.BookingStatus.USER_CANCELLED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.domain.BaseEntity;
import ssafy.batt.domain.booingSeat.BookingSeat;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.payment.Payment;
import ssafy.batt.domain.review.Review;
import ssafy.batt.domain.schedule.PerformanceSchedule;
import ssafy.batt.domain.transfer.Transfer;

@Entity
@Getter
@Table(name = "booking")
@NoArgsConstructor(access = PROTECTED)
public class Booking extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String orderId;

  private Integer seatCount;

  private Integer totalAmount;

  @Enumerated(STRING)
  private BookingStatus status = CONFIRMED;

  @Enumerated(STRING)
  private PaymentMethod paymentMethod;

  private String paymentKey;

  private Instant cancelledDate;

  private Integer cancellationFee;

  @OneToOne(mappedBy = "booking")
  private Payment payment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_schedule_id", nullable = false)
  private PerformanceSchedule performanceSchedule;

  @OneToMany(mappedBy = "booking")
  private final List<BookingSeat> bookingSeats = new ArrayList<>();

  @OneToMany(mappedBy = "booking")
  private final List<Transfer> transfers = new ArrayList<>();

  @OneToMany(mappedBy = "booking")
  private final List<Review> reviews = new ArrayList<>();

  public Booking(String orderId, Integer seatCount, Integer totalAmount,
      PaymentMethod paymentMethod, String paymentKey, Member member,
      PerformanceSchedule performanceSchedule) {
    this.orderId = orderId;
    this.seatCount = seatCount;
    this.totalAmount = totalAmount;
    this.paymentMethod = paymentMethod;
    this.paymentKey = paymentKey;
    this.member = member;
    this.performanceSchedule = performanceSchedule;
  }

  public void updateBookingOrderId() {
    this.orderId = formattingReservationId();
  }

  public void updateBookingStatus(BookingStatus updateStatus) {
    this.status = updateStatus;
  }

  public void cancelBooking(Integer cancellationFee) {
    this.status = USER_CANCELLED;
    this.cancelledDate = now();
    this.cancellationFee = cancellationFee;
  }

  private Booking(String orderId, Integer seatCount, Integer totalAmount, BookingStatus status, Member member,
      PerformanceSchedule performanceSchedule) {
    this.orderId = orderId;
    this.seatCount = seatCount;
    this.totalAmount = totalAmount;
    this.status = status;
    this.member = member;
    this.performanceSchedule = performanceSchedule;
  }

  public static Booking createBookingFromTransfer(Booking originalBooking, Member highestBidder) {
    return new Booking(
        formattingReservationId(),
        originalBooking.getSeatCount(),
        originalBooking.getTotalAmount(),
        PENDING,
        highestBidder,
        originalBooking.getPerformanceSchedule()
    );
  }
}