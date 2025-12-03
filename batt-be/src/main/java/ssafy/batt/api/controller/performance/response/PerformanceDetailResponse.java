package ssafy.batt.api.controller.performance.response;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import ssafy.batt.domain.performance.Genre;
import ssafy.batt.domain.performance.Status;

public record PerformanceDetailResponse(
        Long id,
        String title,
        Integer durationMinute,
        Genre genre,
        String venueName,
        String venueAddress,
        Integer ageRestriction,
        String posterImageUrl,
        String descriptionUrl,
        Status status,
        Instant createdAt,
        String performanceStartDate,
        String performanceEndDate,
        PriceInfo prices
) {
    public static PerformanceDetailResponse of(
            Long id,
            String title,
            Integer durationMinute,
            Genre genre,
            String venueName,
            String venueAddress,
            Integer ageRestriction,
            String posterImageUrl,
            String descriptionUrl,
            Status status,
            Instant createdAt,
            LocalDateTime performanceStartDate,
            LocalDateTime performanceEndDate,
            PriceInfo prices
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        return new PerformanceDetailResponse(
                id,
                title,
                durationMinute,
                genre,
                venueName,
                venueAddress,
                ageRestriction,
                posterImageUrl,
                descriptionUrl,
                status,
                createdAt,
                performanceStartDate != null ? performanceStartDate.format(formatter) : null,
                performanceEndDate != null ? performanceEndDate.format(formatter) : null,
                prices
        );
    }

}