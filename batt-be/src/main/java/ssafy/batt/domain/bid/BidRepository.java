package ssafy.batt.domain.bid;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssafy.batt.domain.transfer.Transfer;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

  Optional<Bid> findFirstByTransferIdOrderByBidAmountDesc(Long transferId);

  boolean existsByTransfer(Transfer transfer);

}
