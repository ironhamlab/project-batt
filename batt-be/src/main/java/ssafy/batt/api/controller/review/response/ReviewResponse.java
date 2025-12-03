package ssafy.batt.api.controller.review.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class ReviewResponse {

  private Long reviewId;

  private Long memberId;

  private Long bookingId;

  private Long performanceId;

  private String performanceTitle;

  private String email;

  private Double rating;

  private String content;

  private boolean hasReviewed;

  private Instant createdAt;

  @Builder
  public ReviewResponse(Long reviewId, Long memberId, Long bookingId, Long performanceId, String performanceTitle,
      String email, Double rating, String content, Instant createdAt) {
    this.reviewId = reviewId;
    this.memberId = memberId;
    this.bookingId = bookingId;
    this.performanceId = performanceId;
    this.performanceTitle = performanceTitle;
    this.email = email;
    this.rating = rating;
    this.content = content;
    this.createdAt = createdAt;
    this.hasReviewed = true;
  }

  public void updateReviewStatus(boolean hasReviewed) {
    this.hasReviewed = hasReviewed;
  }
}