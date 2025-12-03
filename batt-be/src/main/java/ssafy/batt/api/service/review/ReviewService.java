package ssafy.batt.api.service.review;

import static ssafy.batt.common.constant.ConstantUtil.REVIEW_POINT;
import static ssafy.batt.common.exception.booking.BookingErrorCode.BOOKING_NOT_FOUND;
import static ssafy.batt.common.exception.member.MemberErrorCode.MEMBER_NOT_FOUND;
import static ssafy.batt.domain.coin.TransactionType.REVIEW;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.api.controller.coin.response.RemainCoinResponse;
import ssafy.batt.api.controller.review.response.MemberReviewPageResponse;
import ssafy.batt.api.controller.review.response.PerformanceReviewPageResponse;
import ssafy.batt.api.controller.review.response.ReviewResponse;
import ssafy.batt.api.service.coin.CoinService;
import ssafy.batt.api.service.review.request.ReviewCreateServiceRequest;
import ssafy.batt.common.exception.booking.BookingException;
import ssafy.batt.common.exception.member.MemberException;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.booking.BookingRepository;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.member.MemberRepository;
import ssafy.batt.domain.review.Review;
import ssafy.batt.domain.review.ReviewRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

  private final CoinService coinService;
  private final ReviewRepository reviewRepository;
  private final MemberRepository memberRepository;
  private final BookingRepository bookingRepository;

  public MemberReviewPageResponse getMyPageReviewsBy(Long memberId, Pageable pageable) {
    Page<ReviewResponse> reviewResponses = reviewRepository.getMyPageReviewsBy(memberId, pageable);
    return MemberReviewPageResponse.from(reviewResponses);
  }

  public PerformanceReviewPageResponse getPerformanceReviewsBy(Long performanceId, Pageable pageable) {
    Page<ReviewResponse> reviewResponses = reviewRepository.getPerformanceReviewsBy(performanceId, pageable);
    double averageRating = calculateFormattedAverageRating(reviewResponses);
    return PerformanceReviewPageResponse.from(reviewResponses, averageRating);
  }

  @Transactional
  public RemainCoinResponse createReview(ReviewCreateServiceRequest request, Long memberId, Long bookingId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new BookingException(BOOKING_NOT_FOUND));

    coinService.updateCoinTransaction(member, REVIEW_POINT, REVIEW);
    reviewRepository.save(Review.builder()
        .member(member)
        .booking(booking)
        .performance(booking.getPerformanceSchedule().getPerformance())
        .rating(request.rating())
        .content(request.content())
        .build());

    return new RemainCoinResponse(member.getCoinBalance());
  }

  @Transactional
  public void updateReview(ReviewCreateServiceRequest request, Long reviewId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + reviewId));

    review.update(request);
  }

  private double calculateFormattedAverageRating(Page<ReviewResponse> reviewResponses) {
    return Double.parseDouble(String.format("%.2f", reviewResponses.getContent().stream()
        .mapToDouble(ReviewResponse::getRating)
        .average().orElse(0.0)));
  }

  public ReviewResponse getReviewByMemberIdAndBookingId(Member member, Long memberId, Long bookingId) {

    boolean hasReviewed = reviewRepository.existsByMemberIdAndBookingId(memberId, bookingId);

    if (!hasReviewed) {
      ReviewResponse reviewResponse = ReviewResponse.builder().build();
      reviewResponse.updateReviewStatus(false);
      return reviewResponse;
    }

    Review review = reviewRepository.findByMemberIdAndBookingId(memberId, bookingId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

    return ReviewResponse.builder()
        .reviewId(review.getId())
        .content(review.getContent())
        .rating(review.getRating())
        .build();
  }
}