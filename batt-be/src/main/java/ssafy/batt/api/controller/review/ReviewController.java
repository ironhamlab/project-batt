package ssafy.batt.api.controller.review;

import static org.springframework.http.HttpStatus.OK;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssafy.batt.api.controller.coin.response.RemainCoinResponse;
import ssafy.batt.api.controller.review.request.ReviewCreateRequest;
import ssafy.batt.api.controller.review.response.MemberReviewPageResponse;
import ssafy.batt.api.controller.review.response.PerformanceReviewPageResponse;
import ssafy.batt.api.controller.review.response.ReviewResponse;
import ssafy.batt.api.service.review.ReviewService;
import ssafy.batt.api.service.review.request.ReviewCreateServiceRequest;
import ssafy.batt.domain.member.Member;

@RestController
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @GetMapping(value = "/api/v1/reviews", params = "memberId")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<MemberReviewPageResponse> getMyPageReviewsBy(
      @RequestParam("memberId") Long memberId,
      Pageable pageable
  ) {
    MemberReviewPageResponse memberReviewPageResponse = reviewService.getMyPageReviewsBy(memberId, pageable);
    return ResponseEntity.status(OK).body(memberReviewPageResponse);
  }

  @GetMapping(value = "api/v1/reviews", params = "performanceId")
  public ResponseEntity<PerformanceReviewPageResponse> getPerformanceReviewsBy(
      @RequestParam("performanceId") Long performanceId,
      Pageable pageable
  ) {
    PerformanceReviewPageResponse performanceReviewPageResponse = reviewService
        .getPerformanceReviewsBy(performanceId, pageable);
    return ResponseEntity.status(OK).body(performanceReviewPageResponse);
  }

  @GetMapping(value = "/api/v1/reviews", params = {"memberId", "bookingId"})
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Map<String, ReviewResponse>> getReviewByMemberAndBooking(
      @RequestParam("memberId") Long memberId,
      @RequestParam("bookingId") Long bookingId,
      Member member
  ) {
    ReviewResponse reviewResponse = reviewService.getReviewByMemberIdAndBookingId(member, memberId, bookingId);
    return ResponseEntity.status(OK).body(Map.of("reviewInfo", reviewResponse));
  }

  @PostMapping("/api/v1/members/{memberId}/bookings/{bookingId}/reviews")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<RemainCoinResponse> createReview(
      @PathVariable Long memberId,
      @PathVariable Long bookingId,
      @RequestBody ReviewCreateRequest request
  ) {
    RemainCoinResponse remainCoinResponse = reviewService
        .createReview(ReviewCreateServiceRequest.from(request), memberId, bookingId);
    return ResponseEntity.status(OK).body(remainCoinResponse);
  }

  @PatchMapping("/api/v1/reviews/{reviewId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<HttpStatus> updateReview(
      @PathVariable Long reviewId,
      @RequestBody ReviewCreateRequest request
  ) {
    reviewService.updateReview(ReviewCreateServiceRequest.from(request), reviewId);
    return ResponseEntity.status(OK).build();
  }
}
