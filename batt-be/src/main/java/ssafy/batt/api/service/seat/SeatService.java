package ssafy.batt.api.service.seat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.api.controller.seat.response.SeatHoldResponse;
import ssafy.batt.api.controller.seat.response.SeatReleaseResponse;
import ssafy.batt.api.controller.seat.response.SeatResponse;
import ssafy.batt.common.exception.seat.SeatErrorCode;
import ssafy.batt.common.exception.seat.SeatException;
import ssafy.batt.domain.booingSeat.BookingSeatRepository;
import ssafy.batt.domain.booking.BookingStatus;
import ssafy.batt.domain.seat.Seat;
import ssafy.batt.domain.seat.SeatRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {

  private final SeatRepository seatRepository;
  private final SeatRedisService seatRedisService;
  private final BookingSeatRepository bookingSeatRepository;

  public List<SeatResponse> getSeatsByPerformanceScheduleId(Long performanceScheduleId) {
    List<Seat> seats = seatRepository.findAllByOrderByIdAsc();

    return seats.stream()
        .map(seat -> {
          Long heldByUserId = seatRedisService.getHoldingUser(seat.getId(), performanceScheduleId);
          boolean isAvailable = determineAvailability(seat.getId(), performanceScheduleId, heldByUserId);

          return SeatResponse.of(seat, isAvailable, heldByUserId);
        })
        .toList();
  }

  private boolean determineAvailability(Long seatId, Long performanceScheduleId, Long heldByUserId) {

    boolean isConfirmed = bookingSeatRepository.existsBySeatIdAndBookingPerformanceScheduleIdAndBookingStatus(
        seatId, performanceScheduleId, BookingStatus.CONFIRMED);

    boolean isTransferPending = bookingSeatRepository.existsBySeatIdAndBookingPerformanceScheduleIdAndBookingStatus(
        seatId, performanceScheduleId, BookingStatus.TRANSFER_PENDING);

    boolean isTransferred = bookingSeatRepository.existsBySeatIdAndBookingPerformanceScheduleIdAndBookingStatus(
        seatId, performanceScheduleId, BookingStatus.TRANSFERRED);

    if (isConfirmed || isTransferPending || isTransferred || heldByUserId != null) {
      return false;
    } else {
      return true;
    }
  }

  @Transactional
  public SeatHoldResponse holdSeats(List<Long> seatIds, Long performanceScheduleId, Long userId) {
    List<Long> heldSeatIds = new ArrayList<>();
    List<Long> failedSeatIds = new ArrayList<>();

    for (Long seatId : seatIds) {
      if (holdSeat(seatId, performanceScheduleId, userId)) {
        heldSeatIds.add(seatId);
      } else {
        failedSeatIds.add(seatId);
      }
    }

    if (!failedSeatIds.isEmpty()) {
      throw new SeatException(SeatErrorCode.SEAT_ALREADY_HELD);
    }

    Instant holdExpiresAt = Instant.now().plus(seatRedisService.getHoldDuration());

    return new SeatHoldResponse(performanceScheduleId, heldSeatIds, failedSeatIds, holdExpiresAt);
  }

  @Transactional
  public boolean holdSeat(Long seatId, Long performanceScheduleId, Long userId) {
    Seat seat = seatRepository.findById(seatId)
        .orElseThrow(() -> new SeatException(SeatErrorCode.SEAT_NOT_FOUND));

    boolean isConfirmed = bookingSeatRepository.existsBySeatIdAndBookingPerformanceScheduleIdAndBookingStatus(
        seatId, performanceScheduleId, BookingStatus.CONFIRMED);

    boolean isTransferPending = bookingSeatRepository.existsBySeatIdAndBookingPerformanceScheduleIdAndBookingStatus(
        seatId, performanceScheduleId, BookingStatus.TRANSFER_PENDING);

    boolean isTransferred = bookingSeatRepository.existsBySeatIdAndBookingPerformanceScheduleIdAndBookingStatus(
        seatId, performanceScheduleId, BookingStatus.TRANSFERRED);

    if (isConfirmed || isTransferPending || isTransferred) {
      return false;
    }

    return seatRedisService.holdSeat(seatId, performanceScheduleId, userId);
  }

  @Transactional
  public SeatReleaseResponse releaseSeatHolds(List<Long> seatIds, Long performanceScheduleId, Long userId) {
    List<Long> releasedSeatIds = new ArrayList<>();
    List<Long> failedSeatIds = new ArrayList<>();

    for (Long seatId : seatIds) {
      if (seatRedisService.isHeldBySameUser(seatId, performanceScheduleId, userId)) {
        seatRedisService.releaseSeat(seatId, performanceScheduleId);
        releasedSeatIds.add(seatId);
      } else {
        failedSeatIds.add(seatId);
      }
    }

    return new SeatReleaseResponse(releasedSeatIds, failedSeatIds);
  }
}