package ssafy.batt.api.controller.performance.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ssafy.batt.domain.performance.Genre;
import ssafy.batt.domain.performance.Status;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class PerformanceListResponse {
    private final PageInfo pageInfo;
    private final List<PerformanceResponse> performances;

    @Getter
    @RequiredArgsConstructor
    public static class PageInfo {
        private final int totalElements;
        private final int totalPages;
        private final int currentPage;
        private final int pageSize;
    }

    @Getter
    @RequiredArgsConstructor
    public static class PerformanceResponse {

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