package ssafy.batt.api.controller.token.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MobileJwtResponse {
  private String token;
  private Long memberId;
}
