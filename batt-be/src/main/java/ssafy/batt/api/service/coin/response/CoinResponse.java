package ssafy.batt.api.service.coin.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.domain.coin.TransactionType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoinResponse {

  private Long id;

  private Integer amount;

  private String description;

  private TransactionType status;

  private Integer remainCoin;

  private Instant logCreatedAt;

}