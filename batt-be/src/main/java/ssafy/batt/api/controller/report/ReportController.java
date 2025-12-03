package ssafy.batt.api.controller.report;

import static org.springframework.http.HttpStatus.OK;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ssafy.batt.api.controller.report.request.ReportCreateRequest;
import ssafy.batt.api.service.report.ReportService;
import ssafy.batt.api.service.report.request.ReportCreateServiceRequest;

@RestController
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  @PostMapping("/api/v1/reviews/{reviewId}/members/{memberId}/reports")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<HttpStatus> reportReview(
      @PathVariable Long reviewId,
      @PathVariable Long memberId,
      @RequestBody ReportCreateRequest request
  ) {
    reportService.createReviewReport(ReportCreateServiceRequest.from(request), reviewId, memberId);
    return ResponseEntity.status(OK).build();
  }
}