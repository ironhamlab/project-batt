package ssafy.batt.domain.booingSeat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssafy.batt.domain.booking.Booking;
import java.util.List;
import ssafy.batt.domain.booking.BookingStatus;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Integer> {
    
    List<BookingSeat> findByBooking(Booking booking);

    boolean existsBySeatIdAndBookingPerformanceScheduleIdAndBookingStatus(Long seatId, Long performanceScheduleId, BookingStatus status);
}
