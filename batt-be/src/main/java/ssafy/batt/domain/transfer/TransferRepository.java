package ssafy.batt.domain.transfer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long>, TransferRepositoryCustom {

  boolean existsBySellerIdAndBookingId(Long sellerId, Long bookingId);

}
