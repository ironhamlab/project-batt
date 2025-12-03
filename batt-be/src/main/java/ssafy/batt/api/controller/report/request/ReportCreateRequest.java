package ssafy.batt.api.controller.report.request;

import ssafy.batt.domain.report.ReportReason;

public record ReportCreateRequest(
    ReportReason reason,
    String content
) {

}