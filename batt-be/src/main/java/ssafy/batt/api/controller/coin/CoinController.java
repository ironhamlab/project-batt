package ssafy.batt.api.controller.coin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssafy.batt.api.controller.coin.response.CoinPageResponse;
import ssafy.batt.api.controller.coin.response.RemainCoinResponse;
import ssafy.batt.api.service.coin.CoinService;
import ssafy.batt.domain.member.Member;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coins")
public class CoinController {

  private final CoinService coinService;

  @GetMapping
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<CoinPageResponse> getCoinInfo(
      Pageable pageable,
      @RequestParam Long memberId
  ) {
    CoinPageResponse coinPageResponse = coinService.getCoinInfo(pageable, memberId);
    return ResponseEntity.ok().body(coinPageResponse);
  }

  @GetMapping("/refresh")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<RemainCoinResponse> getRemainCoin(Member member) {
    RemainCoinResponse remainCoinResponse = new RemainCoinResponse(member.getCoinBalance());
    return ResponseEntity.ok().body(remainCoinResponse);
  }
}