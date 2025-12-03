package ssafy.batt.domain.booking;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BookingStatus {
  PENDING("결제 대기 중"),
  CONFIRMED("예매 완료"),
  TRANSFER_PENDING("양도 진행 중"),
  USER_CANCELLED("사용자 취소"),
  TRANSFER_FAILED_AUTO_CANCELLED("양도 실패로 자동 취소"),
  TRANSFERRED("양도 완료");

  private final String description;
}