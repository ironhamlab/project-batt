package ssafy.batt.domain.booking;

import static java.time.LocalDate.now;
import static ssafy.batt.domain.booking.BookingStatus.CONFIRMED;
import static ssafy.batt.domain.booking.QBooking.booking;
import static ssafy.batt.domain.performance.QPerformance.performance;
import static ssafy.batt.domain.schedule.QPerformanceSchedule.performanceSchedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ssafy.batt.api.service.booking.response.BookingResponse;

@Slf4j
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<BookingResponse> getMyPageBookingsBy(Pageable pageable, Long memberId) {
    List<Booking> bookings = queryFactory
        .selectFrom(booking)
        .innerJoin(booking.performanceSchedule, performanceSchedule).fetchJoin()
        .innerJoin(booking.performanceSchedule.performance, performance).fetchJoin()
        .leftJoin(booking.bookingSeats).fetchJoin()
        .where(booking.member.id.eq(memberId))
        .orderBy(booking.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    List<BookingResponse> content = bookings.stream()
        .map(booking -> BookingResponse.of(
            booking, null, booking.getCreatedAt())
        )
        .toList();

    Long countResult = queryFactory
        .select(booking.count())
        .from(booking)
        .where(booking.member.id.eq(memberId))
        .fetchOne();

    long total = countResult != null ? countResult : 0L;
    return new PageImpl<>(content, pageable, total);
  }

  @Override
  public List<BookingResponse> getMyPageBookingsBy(Long memberId) {
    List<Booking> bookings = queryFactory
        .selectFrom(booking)
        .innerJoin(booking.performanceSchedule, performanceSchedule).fetchJoin()
        .innerJoin(booking.performanceSchedule.performance, performance).fetchJoin()
        .leftJoin(booking.bookingSeats).fetchJoin()
        .where(booking.member.id.eq(memberId)
            .and(booking.performanceSchedule.performanceDate.between(now(), now().plusDays(20))
                .and(booking.status.eq(CONFIRMED))
        ))
        .orderBy(
            booking.performanceSchedule.performanceDate.asc(),
            booking.performanceSchedule.performanceTime.asc()
        )
        .fetch();

    return bookings.stream()
        .map(booking -> BookingResponse.of(booking, null, null))
        .toList();
  }
}