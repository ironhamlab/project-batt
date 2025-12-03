package ssafy.batt.api.controller.token.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MobileLoginRequest {
  private String provider;
  private String accessToken;
}
