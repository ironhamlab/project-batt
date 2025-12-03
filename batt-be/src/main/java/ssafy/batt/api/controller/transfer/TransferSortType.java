package ssafy.batt.api.controller.transfer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransferSortType {

  POPULAR("인기순"),
  PERFORMANCE_DATE("공연 임박순"),
  REVIEW_COUNT("리뷰 많은 순"),
  LATEST("최신순");

  private final String description;
}