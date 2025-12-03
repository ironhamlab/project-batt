package ssafy.batt.domain.review;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

  boolean existsByMemberIdAndBookingId(Long memberId, Long bookingId);

  Optional<Review> findByMemberIdAndBookingId(Long memberId, Long bookingId);

}