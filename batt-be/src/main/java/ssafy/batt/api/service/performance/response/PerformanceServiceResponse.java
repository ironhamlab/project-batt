package ssafy.batt.api.service.performance.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ssafy.batt.domain.performance.Genre;
import ssafy.batt.domain.performance.Status;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class PerformanceServiceResponse {
    private final List<PerformanceInfo> performances;
    private final long totalElements;
    private final int totalPages;

    @Getter
    @RequiredArgsConstructor
    public static class PerformanceInfo {

        private final Long id;

        private final String title;

        private final String venueName;

        private final Genre genre;

        private final String posterImageUrl;

        private final Status status;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private final LocalDateTime bookingOpenDate;

        private final String performanceStartDate;

        private final String performanceEndDate;
    }
}