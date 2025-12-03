package ssafy.batt.api.controller.booking;

import static ssafy.batt.common.snowflake.Snowflake.formattingReservationId;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssafy.batt.api.controller.booking.response.BookingDetailResponse;
import ssafy.batt.api.controller.booking.response.SnowflakeResponse;
import ssafy.batt.api.service.booking.BookingService;
import ssafy.batt.domain.member.Member;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

  private final BookingService bookingService;

  @GetMapping(params = "memberId")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> getMyPageBookingInfo(
      Member member,
      Optional<Integer> page,
      Optional<Integer> size,
      @RequestParam Long memberId
  ) {
    Object myPageBookingResponse = bookingService.getMyPageBookingInfo(page, size, member, memberId);
    return ResponseEntity.ok().body(myPageBookingResponse);
  }

  @GetMapping(params = {"memberId", "bookingId"})
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<BookingDetailResponse> getBookingDetailInfo(
      Member member,
      @RequestParam Long memberId,
      @RequestParam Long bookingId
  ) {
    BookingDetailResponse bookingDetailResponse = bookingService.getBookingDetailInfo(member, memberId, bookingId);
    return ResponseEntity.ok().body(bookingDetailResponse);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/snowflake/generate")
  public ResponseEntity<SnowflakeResponse> generateReservationId() {
    String snowflakeId = formattingReservationId();
    return ResponseEntity.ok().body(new SnowflakeResponse(snowflakeId));
  }
}