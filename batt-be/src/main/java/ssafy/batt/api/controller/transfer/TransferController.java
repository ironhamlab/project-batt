package ssafy.batt.api.controller.transfer;

import static org.springframework.http.HttpStatus.OK;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssafy.batt.api.controller.transfer.request.BookingTransferCreateRequest;
import ssafy.batt.api.controller.transfer.response.TransferDetailPageResponse;
import ssafy.batt.api.controller.transfer.response.TransferPageResponse;
import ssafy.batt.api.service.booking.request.BookingTransferCreateServiceRequest;
import ssafy.batt.api.service.transfer.TransferService;
import ssafy.batt.domain.member.Member;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TransferController {

  private final TransferService transferService;

  @GetMapping(value = "transfers", params = "sort")
  public ResponseEntity<TransferPageResponse> getPerformanceTransferTickets(
      Pageable pageable,
      @RequestParam(defaultValue = "POPULAR") TransferSortType sort
  ) {
    TransferPageResponse transferPageResponse = transferService.getPerformanceTransferTickets(pageable, sort);
    return ResponseEntity.status(OK).body(transferPageResponse);
  }

  @GetMapping(value = "transfers", params = "performanceId")
  public ResponseEntity<TransferDetailPageResponse> getPerformanceTransferTicketDetails(
      Pageable pageable,
      @RequestParam Long performanceId
  ) {
    TransferDetailPageResponse transferDetailPageResponse =
        transferService.getPerformanceTransferTicketDetails(pageable, performanceId);
    return ResponseEntity.status(OK).body(transferDetailPageResponse);
  }

  @PostMapping("bookings/{bookingId}/transfers")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<HttpStatus> createTransfer(
      Member member,
      @PathVariable Long bookingId,
      @RequestBody BookingTransferCreateRequest request
  ) {
    transferService.createTransfer(member, BookingTransferCreateServiceRequest.from(request), bookingId);
    return ResponseEntity.ok().build();
  }
}
