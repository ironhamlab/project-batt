package ssafy.batt.domain.transfer;

import static com.querydsl.core.types.dsl.Expressions.numberTemplate;
import static com.querydsl.jpa.JPAExpressions.select;
import static java.time.LocalDateTime.now;
import static ssafy.batt.domain.booking.QBooking.booking;
import static ssafy.batt.domain.performance.QPerformance.performance;
import static ssafy.batt.domain.review.QReview.review;
import static ssafy.batt.domain.schedule.QPerformanceSchedule.performanceSchedule;
import static ssafy.batt.domain.transfer.QTransfer.transfer;
import static ssafy.batt.domain.transfer.Status.ACTIVE;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ssafy.batt.api.controller.transfer.TransferSortType;
import ssafy.batt.api.service.transfer.info.TransferParticipantInfo;
import ssafy.batt.api.service.transfer.response.TransferDetailResponse;
import ssafy.batt.api.service.transfer.response.TransferResponse;
import ssafy.batt.domain.bid.Bid;

@RequiredArgsConstructor
public class TransferRepositoryImpl implements TransferRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<TransferResponse> getPerformanceTransferTickets(Pageable pageable, TransferSortType sortType) {
    List<TransferResponse> content = queryFactory
        .select(Projections.constructor(TransferResponse.class,
            performance.id,
            performance.posterImageUrl,
            performance.title,
            performance.venueName,
            performance.performanceStartDate,
            performance.performanceEndDate,
            select(transfer.count())
                .from(transfer)
                .innerJoin(transfer.booking, booking)
                .innerJoin(booking.performanceSchedule, performanceSchedule)
                .where(performanceSchedule.performance.eq(performance)
                    .and(transfer.status.eq(ACTIVE)))
        ))
        .from(performance)
        .where(performance.performanceStartDate.loe(now())
            .and(performance.performanceEndDate.goe(now())))
        .orderBy(getOrderSpecifier(sortType))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long countResult = queryFactory
        .select(performance.count())
        .from(performance)
        .where(performance.performanceStartDate.loe(now())
            .and(performance.performanceEndDate.goe(now())))
        .fetchOne();

    long total = countResult != null ? countResult : 0L;
    return new PageImpl<>(content, pageable, total);
  }

  @Override
  public Page<TransferDetailResponse> getPerformanceTransferTicketDetails(Pageable pageable, Long performanceId) {

    List<Transfer> transfers = queryFactory
        .selectFrom(transfer)
        .innerJoin(transfer.booking, booking)
        .innerJoin(booking.performanceSchedule, performanceSchedule)
        .innerJoin(performanceSchedule.performance, performance)
        .where(performance.id.eq(performanceId))
        .orderBy(
            getTransferStatusCaseExpression().asc(),
            transfer.transferEndDateTime.asc()
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    List<TransferDetailResponse> content = transfers
        .stream()
        .map(transfer -> {
          Long transferSellerId = transfer.getSeller().getId();
          Long highestBidderId = transfer.getBids().stream()
              .max(Comparator.comparing(Bid::getBidAmount))
              .map(bid -> bid.getBidder().getId())
              .orElse(null);

          TransferParticipantInfo participantInfo = new TransferParticipantInfo(transferSellerId, highestBidderId);
          return TransferDetailResponse.from(transfer, transfer.getBooking(), participantInfo);
        })
        .toList();

    Long countResult = queryFactory
        .select(transfer.count())
        .from(transfer)
        .innerJoin(transfer.booking, booking)
        .innerJoin(booking.performanceSchedule, performanceSchedule)
        .innerJoin(performanceSchedule.performance, performance)
        .where(performance.id.eq(performanceId))
        .fetchOne();

    long total = countResult != null ? countResult : 0L;
    return new PageImpl<>(content, pageable, total);
  }

  private OrderSpecifier<?> getOrderSpecifier(TransferSortType sortType) {
    return switch (sortType) {
      case POPULAR -> getAvgRatingSubquery().desc();
      case REVIEW_COUNT -> getReviewCountSubquery().desc();
      case PERFORMANCE_DATE -> performance.performanceStartDate.asc();
      case LATEST -> performance.performanceStartDate.desc();
    };
  }

  private NumberExpression<Double> getAvgRatingSubquery() {
    return numberTemplate(Double.class,
        "({0})",
        select(review.rating.avg().coalesce(0.0))
            .from(review)
            .where(review.performance.id.eq(performance.id))
    );
  }

  private NumberExpression<Long> getReviewCountSubquery() {
    return numberTemplate(Long.class,
        "({0})",
        select(review.count())
            .from(review)
            .where(review.performance.id.eq(performance.id))
    );
  }

  private NumberExpression<Integer> getTransferStatusCaseExpression() {
    return Expressions.cases()
        .when(transfer.status.eq(ACTIVE))
        .then(0)
        .otherwise(1);
  }
}