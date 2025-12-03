package ssafy.batt.api.service.performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ssafy.batt.api.controller.performance.response.PerformanceListResponse;
import ssafy.batt.api.controller.performance.response.PerformanceScheduleResponse;
import ssafy.batt.api.controller.performance.response.PerformanceDetailResponse;
import ssafy.batt.api.service.performance.response.PerformanceServiceResponse;
import ssafy.batt.domain.performance.Performance;
import ssafy.batt.domain.performance.PerformanceRepository;
import ssafy.batt.domain.performance.Status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {
    
    private static final int PERFORMANCE_END_HOUR = 17;
    private static final int PERFORMANCE_END_MINUTE = 0;

    private final PerformanceRepository performanceRepository;
    
    @Value("${performance.scheduler.status-update-interval}")
    private long statusUpdateInterval;
    
    @Value("${performance.scheduler.end-update-interval}")
    private long endUpdateInterval;
    
    @Value("${performance.scheduler.batch-size}")
    private int batchSize;

    public PerformanceListResponse getPerformances(String sort, int limit, int page, String status, String keyword) {
        Pageable pageable = createPageable(page, limit);
        List<Status> statusList = parseStatusParameter(status);
        PerformanceServiceResponse serviceResponse = performanceRepository.findPerformancesWithSort(sort, statusList, keyword, pageable);
        
        return createPerformanceListResponse(serviceResponse, page, limit);
    }
    
    private List<Status> parseStatusParameter(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        
        return Arrays.stream(status.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(Status::valueOf)
                .collect(Collectors.toList());
    }

    private Pageable createPageable(int page, int limit) {
        return PageRequest.of(page - 1, limit);
    }

    private PerformanceListResponse createPerformanceListResponse(PerformanceServiceResponse serviceResponse, int page, int limit) {
        List<PerformanceListResponse.PerformanceResponse> performances = mapToPerformanceResponses(serviceResponse.getPerformances());
        PerformanceListResponse.PageInfo pageInfo = createPageInfo(serviceResponse, page, limit);
        
        return new PerformanceListResponse(pageInfo, performances);
    }

    private List<PerformanceListResponse.PerformanceResponse> mapToPerformanceResponses(List<PerformanceServiceResponse.PerformanceInfo> performances) {
        return performances.stream()
                .map(this::mapToPerformanceResponse)
                .toList();
    }

    private PerformanceListResponse.PerformanceResponse mapToPerformanceResponse(PerformanceServiceResponse.PerformanceInfo info) {
        return new PerformanceListResponse.PerformanceResponse(
                info.getId(),
                info.getTitle(),
                info.getVenueName(),
                info.getGenre(),
                info.getPosterImageUrl(),
                info.getStatus(),
                info.getBookingOpenDate(),
                info.getPerformanceStartDate(),
                info.getPerformanceEndDate()
        );
    }

    private PerformanceListResponse.PageInfo createPageInfo(PerformanceServiceResponse serviceResponse, int page, int limit) {
        return new PerformanceListResponse.PageInfo(
                (int) serviceResponse.getTotalElements(),
                serviceResponse.getTotalPages(),
                page,
                limit
        );
    }

    public PerformanceScheduleResponse getPerformanceSchedule(Long performanceId) {
        return performanceRepository.findPerformanceScheduleById(performanceId);
    }

    public PerformanceDetailResponse getPerformanceDetail(Long performanceId) {
        return performanceRepository.findPerformanceDetailById(performanceId);
    }

    @Scheduled(fixedRateString = "${performance.scheduler.status-update-interval}")
    @Transactional
    public void updatePerformanceStatusToOpen() {
        LocalDateTime now = LocalDateTime.now();
        
        List<Performance> performancesToOpen = performanceRepository
            .findByStatusAndBookingOpenDateBefore(Status.SCHEDULE, now);
        
        if (performancesToOpen.isEmpty()) {
            return;
        }
        
        int totalUpdated = 0;
        for (int i = 0; i < performancesToOpen.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, performancesToOpen.size());
            List<Performance> batch = performancesToOpen.subList(i, endIndex);
            
            for (Performance performance : batch) {
                performance.openBooking();
                log.debug("공연 예매 오픈: performanceId={}, title={}, bookingOpenDate={}", 
                    performance.getId(), performance.getTitle(), performance.getBookingOpenDate());
            }
            
            performanceRepository.saveAll(batch);
            totalUpdated += batch.size();
        }
        
        log.info("총 {}개 공연의 상태가 OPEN으로 변경되었습니다.", totalUpdated);
    }

    @Scheduled(fixedRateString = "${performance.scheduler.end-update-interval}")
    @Transactional
    public void updatePerformanceStatusToEnd() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endCutoffTime = calculateEndCutoffTime(now);
        
        List<Status> activeStatuses = Arrays.asList(Status.SCHEDULE, Status.OPEN);
        List<Performance> performancesToEnd = performanceRepository
            .findByStatusInAndPerformanceEndDateBefore(activeStatuses, endCutoffTime);
        
        if (performancesToEnd.isEmpty()) {
            return;
        }
        
        int totalUpdated = 0;
        for (int i = 0; i < performancesToEnd.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, performancesToEnd.size());
            List<Performance> batch = performancesToEnd.subList(i, endIndex);
            
            for (Performance performance : batch) {
                performance.endPerformance();
                log.debug("공연 종료: performanceId={}, title={}, endDate={}", 
                    performance.getId(), performance.getTitle(), performance.getPerformanceEndDate());
            }
            
            performanceRepository.saveAll(batch);
            totalUpdated += batch.size();
        }
        
        log.info("총 {}개 공연의 상태가 END로 변경되었습니다.", totalUpdated);
    }
    
    private LocalDateTime calculateEndCutoffTime(LocalDateTime now) {
        return now.toLocalDate().plusDays(1).atTime(PERFORMANCE_END_HOUR, PERFORMANCE_END_MINUTE);
    }
}