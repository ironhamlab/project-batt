package ssafy.batt.domain.review;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.api.service.review.request.ReviewCreateServiceRequest;
import ssafy.batt.domain.BaseEntity;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.performance.Performance;
import ssafy.batt.domain.report.ReviewReport;

@Entity
@Getter
@Table(name = "review")
@NoArgsConstructor(access = PROTECTED)
public class Review extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "booking_id", nullable = false)
  private Booking booking;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performance_id", nullable = false)
  private Performance performance;

  private double rating;

  @Column(columnDefinition = "TEXT")
  private String content;

  @OneToMany(mappedBy = "review")
  private List<ReviewReport> reports = new ArrayList<>();

  @Builder
  private Review(Member member, Booking booking, Performance performance, double rating,
      String content, List<ReviewReport> reports) {
    this.member = member;
    this.booking = booking;
    this.performance = performance;
    this.rating = rating;
    this.content = content;
    this.reports = reports;
  }

  public void update(ReviewCreateServiceRequest request) {
    this.rating = request.rating();
    this.content = request.content();
  }
}