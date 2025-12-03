package ssafy.batt.api.controller.performance.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceScheduleResponse {

    private Long performanceId;
    
    private List<PerformanceScheduleInfo> performanceSchedule;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceScheduleInfo {
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate performanceDate;
        
        private List<PerformanceTimeInfo> performanceTimeInfo;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceTimeInfo {
        
        private Long performanceScheduleId;
        
        @JsonFormat(pattern = "HH:mm")
        private LocalTime performanceTime;
    }
}