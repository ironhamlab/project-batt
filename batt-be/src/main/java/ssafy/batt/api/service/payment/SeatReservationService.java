package ssafy.batt.api.service.payment;

import static ssafy.batt.common.exception.seat.SeatErrorCode.SEAT_ALREADY_RESERVED;
import static ssafy.batt.common.exception.seat.SeatErrorCode.SEAT_NOT_FOUND;
import static ssafy.batt.common.snowflake.Snowflake.formattingReservationId;
import static ssafy.batt.domain.booking.BookingStatus.CONFIRMED;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.api.controller.payment.request.PaymentSuccessRequest;
import ssafy.batt.common.exception.seat.SeatException;
import ssafy.batt.domain.booingSeat.BookingSeat;
import ssafy.batt.domain.booingSeat.BookingSeatRepository;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.seat.Seat;
import ssafy.batt.domain.seat.SeatRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatReservationService {

  private final SeatRepository seatRepository;
  private final BookingSeatRepository bookingSeatRepository;

  public void validateSeatAvailability(List<Long> seatIds, Long performanceScheduleId) throws SeatException {
    for (Long seatId : seatIds) {
      if (bookingSeatRepository.existsBySeatIdAndBookingPerformanceScheduleIdAndBookingStatus(
          seatId, performanceScheduleId, CONFIRMED)) {
        throw new SeatException(SEAT_ALREADY_RESERVED);
      }
    }
    log.info("좌석 예약 가능 확인 완료 - seatIds: {}, performanceScheduleId: {}", seatIds, performanceScheduleId);
  }

  @Transactional
  public void reserveSeats(PaymentSuccessRequest request, Booking booking) {
    List<Seat> seats = seatRepository.findAllById(request.seatIds());

    if (seats.size() != request.seatIds().size()) {
      throw new SeatException(SEAT_NOT_FOUND);
    }

    for (Seat seat : seats) {
      String bookingNumber = formattingReservationId();

      BookingSeat bookingSeat = new BookingSeat(
          booking,
          seat,
          bookingNumber,
          seat.getPrice()
      );

      bookingSeatRepository.save(bookingSeat);

      log.info("좌석 예약 완료 - seatId: {}, bookingNumber: {}, status: RESERVED", seat.getId(), bookingNumber);
    }
  }

  @Transactional
  public void releaseSeats(Booking booking) {
    List<BookingSeat> bookingSeats = bookingSeatRepository.findByBooking(booking);

    for (BookingSeat bookingSeat : bookingSeats) {
      bookingSeat.cancel();
      log.info("좌석 해제 및 취소 처리 - seatId: {}, bookingSeatStatus: CANCELLED", bookingSeat.getSeat().getId());
    }

    bookingSeatRepository.saveAll(bookingSeats);
    log.info("예매좌석 취소 처리 완료 - bookingId: {}, 취소된 좌석 수: {}", booking.getId(), bookingSeats.size());
  }
}