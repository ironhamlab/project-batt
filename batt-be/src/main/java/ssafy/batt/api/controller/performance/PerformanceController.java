package ssafy.batt.api.controller.performance;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssafy.batt.api.controller.performance.response.PerformanceListResponse;
import ssafy.batt.api.controller.performance.response.PerformanceScheduleResponse;
import ssafy.batt.api.controller.performance.response.PerformanceDetailResponse;
import ssafy.batt.api.service.performance.PerformanceService;
import ssafy.batt.domain.performance.Status;

@RestController
@RequestMapping("/api/v1/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    @GetMapping
    public PerformanceListResponse getPerformances(
            @RequestParam(defaultValue = "rating_desc") String sort,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ) {
        return performanceService.getPerformances(sort, limit, page, status, keyword);
    }

    @GetMapping("/{id}")
    public PerformanceDetailResponse getPerformanceDetail(@PathVariable Long id) {
        return performanceService.getPerformanceDetail(id);
    }

    @GetMapping("/{id}/schedule")
    public PerformanceScheduleResponse getPerformanceSchedule(@PathVariable Long id) {
        return performanceService.getPerformanceSchedule(id);
    }
}