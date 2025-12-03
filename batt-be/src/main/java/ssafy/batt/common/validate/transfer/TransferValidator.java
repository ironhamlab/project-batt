package ssafy.batt.common.validate.transfer;

import static java.time.Instant.now;
import static lombok.AccessLevel.PRIVATE;
import static ssafy.batt.common.exception.transfer.TransferErrorCode.TRANSFER_ALREADY_CLOSED;
import static ssafy.batt.common.exception.transfer.TransferErrorCode.TRANSFER_END_TIME_MUST_BE_BEFORE_PERFORMANCE;
import static ssafy.batt.common.exception.transfer.TransferErrorCode.TRANSFER_END_TIME_MUST_BE_FUTURE;
import static ssafy.batt.domain.transfer.Status.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import lombok.AllArgsConstructor;
import ssafy.batt.common.exception.transfer.TransferException;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.transfer.Status;

@AllArgsConstructor(access = PRIVATE)
public class TransferValidator {

  public static void validateTransferEndDateTime(Booking booking, Instant transferEndDateTime) {

    Instant now = now();
    if (transferEndDateTime.isBefore(now) || transferEndDateTime.equals(now)) {
      throw new TransferException(TRANSFER_END_TIME_MUST_BE_FUTURE);
    }

    LocalDate performanceDate = booking.getPerformanceSchedule().getPerformanceDate();
    LocalTime performanceTime = booking.getPerformanceSchedule().getPerformanceTime();
    LocalDateTime performanceDateTime = LocalDateTime.of(performanceDate, performanceTime);

    Instant performanceInstant = performanceDateTime.atZone(ZoneId.systemDefault()).toInstant();

    if (transferEndDateTime.isAfter(performanceInstant)) {
      throw new TransferException(TRANSFER_END_TIME_MUST_BE_BEFORE_PERFORMANCE);
    }
  }

  public static void validateTransferStatus(Status status) {
    if (status != ACTIVE) {
      throw new TransferException(TRANSFER_ALREADY_CLOSED);
    }
  }
}