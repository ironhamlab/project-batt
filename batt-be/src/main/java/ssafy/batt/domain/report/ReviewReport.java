package ssafy.batt.domain.report;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static ssafy.batt.domain.report.Status.PENDING;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.api.service.report.request.ReportCreateServiceRequest;
import ssafy.batt.domain.BaseEntity;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.review.Review;

@Entity
@Getter
@Table(name = "review_report")
@NoArgsConstructor(access = PROTECTED)
public class ReviewReport extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reporter_id", nullable = false)
  private Member reporter;

  @Enumerated(STRING)
  private ReportReason reportReason;

  private String reportContent;

  @Enumerated(STRING)
  private Status status = PENDING;

  private ReviewReport(Review review, Member reporter, ReportReason reportReason, String reportContent,
      Status status) {
    this.review = review;
    this.reporter = reporter;
    this.reportReason = reportReason;
    this.reportContent = reportContent;
    this.status = status;
  }

  public static ReviewReport of(ReportCreateServiceRequest request, Review review, Member member) {
    return new ReviewReport(review, member, request.reason(), request.content(), PENDING);
  }
}