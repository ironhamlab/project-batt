package ssafy.batt.domain.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ssafy.batt.api.controller.review.response.ReviewResponse;

public interface ReviewRepositoryCustom {

  Page<ReviewResponse> getMyPageReviewsBy(Long memberId, Pageable pageable);

  Page<ReviewResponse> getPerformanceReviewsBy(Long performanceId, Pageable pageable);
}