package ssafy.batt.domain.booking;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

  Optional<Booking> findById(Long bookingId);

  Optional<Booking> findByIdAndMemberId(Long bookingId, Long memberId);
}