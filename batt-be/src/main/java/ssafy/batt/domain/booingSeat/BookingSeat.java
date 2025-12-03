package ssafy.batt.domain.booingSeat;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static ssafy.batt.common.snowflake.Snowflake.formattingReservationId;
import static ssafy.batt.domain.booingSeat.BookingSeatStatus.RESERVED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.domain.BaseEntity;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.seat.Seat;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "booking_seat")
public class BookingSeat extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "booking_id", nullable = false)
  private Booking booking;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seat_id", nullable = false)
  private Seat seat;

  @Column(unique = true, nullable = false)
  private String bookingNumber;

  private Integer price;

  @Enumerated(STRING)
  private BookingSeatStatus status = RESERVED;

  public BookingSeat(Booking booking, Seat seat, String bookingNumber, Integer price) {
    this.booking = booking;
    this.seat = seat;
    this.bookingNumber = bookingNumber;
    this.price = price;
  }

  public void cancel() {
    this.status = BookingSeatStatus.CANCELLED;
  }

  public void updateBookingNumber(String bookingNumber) {
    this.bookingNumber = bookingNumber;
  }

  public static BookingSeat of(Booking booking, Seat seat, Integer price) {
    return new BookingSeat(booking, seat, formattingReservationId(), price);
  }
}
