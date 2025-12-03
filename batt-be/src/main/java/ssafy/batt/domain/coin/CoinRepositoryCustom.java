package ssafy.batt.domain.coin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ssafy.batt.api.service.coin.response.CoinResponse;

public interface CoinRepositoryCustom {

  Page<CoinResponse> getCoinInfo(Pageable pageable, Long memberId);

}