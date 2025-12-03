package ssafy.batt.domain.performance;

import java.util.List;
import org.springframework.data.domain.Pageable;
import ssafy.batt.api.service.performance.response.PerformanceServiceResponse;
import ssafy.batt.api.controller.performance.response.PerformanceScheduleResponse;
import ssafy.batt.api.controller.performance.response.PerformanceDetailResponse;

public interface PerformanceRepositoryCustom {
    PerformanceServiceResponse findPerformancesWithSort(String sort, List<Status> statusList, String keyword, Pageable pageable);
    PerformanceScheduleResponse findPerformanceScheduleById(Long performanceId);
    PerformanceDetailResponse findPerformanceDetailById(Long performanceId);
}