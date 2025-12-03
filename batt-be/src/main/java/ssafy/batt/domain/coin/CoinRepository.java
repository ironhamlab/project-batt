package ssafy.batt.domain.coin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long>, CoinRepositoryCustom {

}
