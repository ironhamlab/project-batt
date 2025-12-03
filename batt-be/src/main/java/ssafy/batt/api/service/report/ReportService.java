package ssafy.batt.api.service.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.api.service.report.request.ReportCreateServiceRequest;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.member.MemberRepository;
import ssafy.batt.domain.report.ReviewReport;
import ssafy.batt.domain.report.ReviewReportRepository;
import ssafy.batt.domain.review.Review;
import ssafy.batt.domain.review.ReviewRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

  private final MemberRepository memberRepository;
  private final ReviewRepository reviewRepository;
  private final ReviewReportRepository reviewReportRepository;

  @Transactional
  public void createReviewReport(ReportCreateServiceRequest request, Long reviewId, Long memberId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

    reviewReportRepository.save(ReviewReport.of(request, review, member));
  }
}