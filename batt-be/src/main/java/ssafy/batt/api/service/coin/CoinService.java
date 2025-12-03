package ssafy.batt.api.service.coin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.api.controller.coin.response.CoinPageResponse;
import ssafy.batt.api.service.coin.response.CoinResponse;
import ssafy.batt.domain.coin.Coin;
import ssafy.batt.domain.coin.CoinRepository;
import ssafy.batt.domain.coin.TransactionType;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.member.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoinService {

  private final CoinRepository coinRepository;
  private final MemberRepository memberRepository;

  public CoinPageResponse getCoinInfo(Pageable pageable, Long memberId) {
    Page<CoinResponse> coinResponse = coinRepository.getCoinInfo(pageable, memberId);
    return CoinPageResponse.from(coinResponse);
  }

  @Transactional
  public void updateCoinTransaction(Member member, int amount, TransactionType transactionType) {
    coinRepository.save(Coin.of(member, amount, transactionType, transactionType.getDescription()));
  }
}