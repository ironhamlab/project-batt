package ssafy.batt.api.service.transfer;

import static java.time.ZoneId.systemDefault;
import static ssafy.batt.common.exception.booking.BookingErrorCode.BOOKING_NOT_FOUND;
import static ssafy.batt.common.exception.booking.BookingErrorCode.BOOKING_TRANSFER_DUPLICATE_REGISTRATION;
import static ssafy.batt.common.validate.transfer.TransferValidator.validateTransferEndDateTime;
import static ssafy.batt.domain.booking.BookingStatus.TRANSFER_PENDING;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.api.controller.transfer.TransferSortType;
import ssafy.batt.api.controller.transfer.response.TransferDetailPageResponse;
import ssafy.batt.api.controller.transfer.response.TransferPageResponse;
import ssafy.batt.api.service.booking.request.BookingTransferCreateServiceRequest;
import ssafy.batt.api.service.transfer.response.TransferDetailResponse;
import ssafy.batt.api.service.transfer.response.TransferResponse;
import ssafy.batt.common.exception.booking.BookingException;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.booking.BookingRepository;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.transfer.Transfer;
import ssafy.batt.domain.transfer.TransferRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferService {

  private final TaskScheduler taskScheduler;
  private final BookingRepository bookingRepository;
  private final TransferRepository transferRepository;
  private final TransferSseService transferSseService;
  private final TransferSchedulerService transferSchedulerService;

  public TransferPageResponse getPerformanceTransferTickets(Pageable pageable, TransferSortType sortType) {
    Page<TransferResponse> transferResponses = transferRepository.getPerformanceTransferTickets(pageable, sortType);
    return TransferPageResponse.from(transferResponses);
  }

  public TransferDetailPageResponse getPerformanceTransferTicketDetails(Pageable pageable, Long performanceId) {
    Page<TransferDetailResponse> transferDetailResponses =
        transferRepository.getPerformanceTransferTicketDetails(pageable, performanceId);
    return TransferDetailPageResponse.from(transferDetailResponses);
  }

  @Transactional
  public void createTransfer(Member member, BookingTransferCreateServiceRequest request, Long bookingId) {

    Booking booking = bookingRepository.findById(bookingId).orElseThrow(
        () -> new BookingException(BOOKING_NOT_FOUND)
    );

    Instant transferEndDateTime = request.transferEndDateTime();
    validateTransferEndDateTime(booking, transferEndDateTime);

    if (transferRepository.existsBySellerIdAndBookingId(member.getId(), bookingId)) {
      throw new BookingException(BOOKING_TRANSFER_DUPLICATE_REGISTRATION);
    }

    booking.updateBookingStatus(TRANSFER_PENDING);
    Transfer transfer = transferRepository.save(Transfer.from(member, booking, transferEndDateTime));
    TransferDetailResponse transferDetailResponse = TransferDetailResponse.from(transfer, booking);
    Long performanceId = booking.getPerformanceSchedule().getPerformance().getId();

    transferSseService.sendUpdate(performanceId, transferDetailResponse);
    taskScheduler.schedule(() -> transferSchedulerService.processTransferCompletion(transfer.getId()),
        transfer.getTransferEndDateTime().atZone(systemDefault()).toInstant());
  }
}