package ssafy.batt.domain.performance;

import static ssafy.batt.domain.performance.QPerformance.performance;
import static ssafy.batt.domain.review.QReview.review;
import static ssafy.batt.domain.schedule.QPerformanceSchedule.performanceSchedule;
import static ssafy.batt.domain.seat.QSeat.seat;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ssafy.batt.api.controller.performance.response.PerformanceDetailResponse;
import ssafy.batt.api.controller.performance.response.PerformanceScheduleResponse;
import ssafy.batt.api.controller.performance.response.PriceInfo;
import ssafy.batt.api.service.performance.response.PerformanceServiceResponse;
import ssafy.batt.domain.schedule.PerformanceSchedule;
import ssafy.batt.domain.seat.Grade;

@Repository
@RequiredArgsConstructor
public class PerformanceRepositoryImpl implements PerformanceRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public PerformanceServiceResponse findPerformancesWithSort(String sort, List<Status> statusList, String keyword,
      Pageable pageable) {
    List<PerformanceServiceResponse.PerformanceInfo> performances = fetchPerformances(sort, statusList, keyword, pageable);
    long totalCount = countPerformances(statusList, keyword);
    int totalPages = calculateTotalPages(totalCount, pageable.getPageSize());

    return new PerformanceServiceResponse(performances, totalCount, totalPages);
  }

  private List<PerformanceServiceResponse.PerformanceInfo> fetchPerformances(String sort, List<Status> statusList, String keyword,
      Pageable pageable) {
    return queryFactory
        .select(Projections.constructor(
            PerformanceServiceResponse.PerformanceInfo.class,
            performance.id,
            performance.title,
            performance.venueName,
            performance.genre,
            performance.posterImageUrl,
            performance.status,
            performance.bookingOpenDate,
            performance.performanceStartDate.min().stringValue(),
            performance.performanceEndDate.max().stringValue()
        ))
        .from(performance)
        .leftJoin(performanceSchedule).on(performance.id.eq(performanceSchedule.performance.id))
        .leftJoin(review).on(performance.id.eq(review.performance.id))
        .where(statusCondition(statusList), searchCondition(keyword))
        .groupBy(getGroupByFields())
        .orderBy(getOrderSpecifier(sort))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
  }

  private long countPerformances(List<Status> statusList, String keyword) {
    return Optional.ofNullable(queryFactory
            .select(performance.count())
            .from(performance)
            .where(statusCondition(statusList), searchCondition(keyword))
            .fetchOne())
        .orElse(0L);
  }

  private int calculateTotalPages(long totalCount, int pageSize) {
    return (int) Math.ceil((double) totalCount / pageSize);
  }

  private Expression<?>[] getGroupByFields() {
    return new Expression<?>[]{
        performance.id, performance.title, performance.venueName,
        performance.genre, performance.posterImageUrl, performance.status,
        performance.bookingOpenDate, performance.createdAt
    };
  }

  private BooleanExpression statusCondition(List<Status> statusList) {
    return statusList != null && !statusList.isEmpty() ? performance.status.in(statusList) : null;
  }

  private BooleanExpression searchCondition(String keyword) {
    return keyword != null && !keyword.trim().isEmpty()
        ? performance.title.containsIgnoreCase(keyword)
        : null;
  }

  private OrderSpecifier<?>[] getOrderSpecifier(String sort) {
    OrderSpecifier<?> primaryOrder = getPrimaryOrderSpecifier(sort);
    OrderSpecifier<?> secondaryOrder = performance.createdAt.desc();

    return primaryOrder != null
        ? new OrderSpecifier[]{primaryOrder, secondaryOrder}
        : new OrderSpecifier[]{secondaryOrder};
  }

  private OrderSpecifier<?> getPrimaryOrderSpecifier(String sort) {
    return switch (sort) {
      case "latest" -> null;
      case "upcoming" -> performance.performanceStartDate.min().asc().nullsLast();
      case "review_count" -> review.id.count().desc().nullsLast();
      case "rating_desc" -> review.rating.avg().desc().nullsLast();
      case "rating_asc" -> review.rating.avg().asc().nullsLast();
      default -> review.rating.avg().desc().nullsLast();
    };
  }

  @Override
  public PerformanceScheduleResponse findPerformanceScheduleById(Long performanceId) {

    List<PerformanceSchedule> schedules = queryFactory
        .selectFrom(performanceSchedule)
        .where(performanceSchedule.performance.id.eq(performanceId))
        .orderBy(performanceSchedule.performanceDate.asc(), performanceSchedule.performanceTime.asc())
        .fetch();

    Map<java.time.LocalDate, List<PerformanceSchedule>> groupedByDate = schedules.stream()
        .filter(schedule -> schedule.getPerformanceDate() != null)
        .collect(Collectors.groupingBy(PerformanceSchedule::getPerformanceDate));

    List<PerformanceScheduleResponse.PerformanceScheduleInfo> performanceScheduleInfos =
        groupedByDate.entrySet().stream()
            .map(entry -> {
              List<PerformanceScheduleResponse.PerformanceTimeInfo> timeInfos =
                  entry.getValue().stream()
                      .map(schedule -> new PerformanceScheduleResponse.PerformanceTimeInfo(
                          schedule.getId(),
                          schedule.getPerformanceTime()
                      ))
                      .collect(Collectors.toList());

              return new PerformanceScheduleResponse.PerformanceScheduleInfo(
                  entry.getKey(),
                  timeInfos
              );
            })
            .sorted((a, b) -> a.getPerformanceDate().compareTo(b.getPerformanceDate()))
            .collect(Collectors.toList());

    return new PerformanceScheduleResponse(performanceId, performanceScheduleInfos);
  }

  @Override
  public PerformanceDetailResponse findPerformanceDetailById(Long performanceId) {
    Performance performanceEntity = queryFactory
        .selectFrom(performance)
        .where(performance.id.eq(performanceId))
        .fetchOne();

    if (performanceEntity == null) {
      return null;
    }

    Map<Grade, Integer> priceMap = queryFactory
        .select(seat.grade, seat.price)
        .from(seat)
        .join(seat.performanceSchedule, performanceSchedule)
        .where(performanceSchedule.performance.id.eq(performanceId))
        .fetch()
        .stream()
        .collect(Collectors.toMap(
            tuple -> tuple.get(seat.grade),
            tuple -> tuple.get(seat.price),
            (existing, replacement) -> existing
        ));

    Map<Grade, Integer> priceMapWithDefault = Grade.getDefaultPriceMap(priceMap);
    PriceInfo priceInfo = PriceInfo.from(priceMapWithDefault);

    return PerformanceDetailResponse.of(
        performanceEntity.getId(),
        performanceEntity.getTitle(),
        performanceEntity.getDurationMinute(),
        performanceEntity.getGenre(),
        performanceEntity.getVenueName(),
        performanceEntity.getVenueAddress(),
        performanceEntity.getAgeRestriction(),
        performanceEntity.getPosterImageUrl(),
        performanceEntity.getDescriptionUrl(),
        performanceEntity.getStatus(),
        performanceEntity.getCreatedAt(),
        performanceEntity.getPerformanceStartDate(),
        performanceEntity.getPerformanceEndDate(),
        priceInfo
    );
  }
}