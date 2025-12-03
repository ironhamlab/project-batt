package ssafy.batt.domain.report;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReportReason {

  OFF_TOPIC("주제와 무관"),
  ILLEGAL_CONTENT("불법 콘텐츠"),
  FALSE_INFORMATION("허위 정보"),
  SPAM_OR_ADVERTISEMENT("스팸 및 광고"),
  INAPPROPRIATE_LANGUAGE("부적절한 언어"),
  HARASSMENT_OR_DEFAMATION("명예 훼손 및 괴롭힘"),
  PERSONAL_INFORMATION_EXPOSURE("개인 정보 노출"),
  OTHER("기타");

  private final String description;
}
