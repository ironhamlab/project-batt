package ssafy.batt.domain.coin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {

  SIGN_UP("회원가입"),
  REVIEW("리뷰 작성"),
  TRANSFER_SUCCESS("양도 성공"),
  TRANSFER_FAIL("양도 실패"),
  BID_SUCCESS("경매 입찰 성공"),
  BID_FAIL("경매 입찰 취소");

  private final String description;

}
