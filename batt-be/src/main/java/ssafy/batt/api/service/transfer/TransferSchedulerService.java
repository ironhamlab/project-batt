package ssafy.batt.api.service.transfer;

import static ssafy.batt.common.exception.transfer.TransferErrorCode.TRANSFER_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.api.service.booking.BookingService;
import ssafy.batt.api.service.transfer.response.TransferDetailResponse;
import ssafy.batt.common.exception.transfer.TransferErrorCode;
import ssafy.batt.common.exception.transfer.TransferException;
import ssafy.batt.domain.transfer.Transfer;
import ssafy.batt.domain.transfer.TransferRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferSchedulerService {


  private final BookingService bookingService;
  private final TransferRepository transferRepository;
  private final TransferSseService transferSseService;

  @Transactional
  public void processTransferCompletion(Long transferId) {
    Transfer transfer = transferRepository.findById(transferId).orElseThrow(
        () -> new TransferException(TRANSFER_NOT_FOUND)
    );

    try {
      transfer.closeTransfer();
      transferRepository.save(transfer);
      TransferDetailResponse transferDetailResponse = TransferDetailResponse.from(transfer, transfer.getBooking());
      bookingService.updateBookingDetailsAfterTransfer(transfer);

      Long performanceId = transfer.getBooking().getPerformanceSchedule().getPerformance().getId();
      transferSseService.sendTransferEnd(performanceId, transferDetailResponse);

      log.info("양도 마감 SSE 알림 전송 완료: transferId={}, performanceId={}", transferId, performanceId);

    } catch (Exception e) {
      log.error("양도 마감 처리 중 오류 발생: transferId={}", transferId, e);
      throw new TransferException(TransferErrorCode.TRANSFER_COMPLETION_ERROR);
    }
  }
}
