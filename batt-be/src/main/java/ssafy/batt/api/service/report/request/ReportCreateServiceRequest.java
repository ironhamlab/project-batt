package ssafy.batt.api.service.report.request;

import ssafy.batt.api.controller.report.request.ReportCreateRequest;
import ssafy.batt.domain.report.ReportReason;

public record ReportCreateServiceRequest(
    ReportReason reason,
    String content
) {

  public static ReportCreateServiceRequest from(ReportCreateRequest request) {
    return new ReportCreateServiceRequest(request.reason(), request.content());
  }
}