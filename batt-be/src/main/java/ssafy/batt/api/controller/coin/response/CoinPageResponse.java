package ssafy.batt.api.controller.coin.response;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import ssafy.batt.api.service.coin.response.CoinResponse;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class CoinPageResponse {

  private final PageInfo pageInfo;
  private final List<CoinResponse> coins;

  @Getter
  @AllArgsConstructor(access = PRIVATE)
  public static class PageInfo {

    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
  }

  public static CoinPageResponse from(Page<CoinResponse> coinResponse) {
    PageInfo pageInfo = new PageInfo(
        coinResponse.getTotalElements(),
        coinResponse.getTotalPages(),
        coinResponse.getNumber() + 1,
        coinResponse.getSize()
    );

    List<CoinResponse> coins = coinResponse.getContent();
    return new CoinPageResponse(pageInfo, coins);
  }
}