package ssafy.batt.domain.booking;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ssafy.batt.api.service.booking.response.BookingResponse;

public interface BookingRepositoryCustom {

  Page<BookingResponse> getMyPageBookingsBy(Pageable pageable, Long memberId);

  List<BookingResponse> getMyPageBookingsBy(Long memberId);
}