package ssafy.batt.domain.review;

import static ssafy.batt.domain.booking.QBooking.booking;
import static ssafy.batt.domain.member.QMember.member;
import static ssafy.batt.domain.performance.QPerformance.performance;
import static ssafy.batt.domain.review.QReview.review;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ssafy.batt.api.controller.review.response.ReviewResponse;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<ReviewResponse> getMyPageReviewsBy(Long memberId, Pageable pageable) {
    List<ReviewResponse> content = queryFactory
        .select(Projections.constructor(ReviewResponse.class,
            review.id,
            review.member.id,
            review.booking.id,
            review.performance.id,
            review.performance.title,
            review.member.email,
            review.rating,
            review.content,
            review.createdAt
        ))
        .from(review)
        .innerJoin(review.member, member)
        .innerJoin(review.booking, booking)
        .innerJoin(review.performance, performance)
        .where(review.member.id.eq(memberId))
        .orderBy(review.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long countResult = queryFactory
        .select(review.count())
        .from(review)
        .innerJoin(review.member, member)
        .where(review.member.id.eq(memberId))
        .fetchOne();

    long total = countResult != null ? countResult : 0L;

    return new PageImpl<>(content, pageable, total);
  }

  @Override
  public Page<ReviewResponse> getPerformanceReviewsBy(Long performanceId, Pageable pageable) {

    List<ReviewResponse> content = queryFactory
        .select(Projections.constructor(ReviewResponse.class,
            review.id,
            review.member.id,
            review.booking.id,
            review.performance.id,
            review.performance.title,
            review.member.email,
            review.rating,
            review.content,
            review.createdAt
        ))
        .from(review)
        .innerJoin(review.member, member)
        .innerJoin(review.booking, booking)
        .innerJoin(review.performance, performance)
        .where(review.performance.id.eq(performanceId))
        .orderBy(review.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long countResult = queryFactory
        .select(review.count())
        .from(review)
        .innerJoin(review.performance, performance)
        .where(review.performance.id.eq(performanceId))
        .fetchOne();

    long total = countResult != null ? countResult : 0L;

    return new PageImpl<>(content, pageable, total);
  }
}