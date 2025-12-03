package ssafy.batt.api.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ssafy.batt.api.service.auth.info.NaverUserInfo;
import ssafy.batt.api.service.auth.request.NaverUserCreateServiceRequest;

import java.util.Optional;

import static ssafy.batt.common.constant.ConstantUtil.*;

@Service
@RequiredArgsConstructor
public class NaverApiService {
  private final RestClient restClient;

  public NaverUserInfo getNaverUserInfoFromAccessToken(String accessToken) {
    NaverUserCreateServiceRequest request = restClient.get()
        .uri(NAVER_USER_INFO_URL)
        .header(TOKEN_HEADER, BEARER_PREFIX + accessToken)
        .retrieve()
        .body(NaverUserCreateServiceRequest.class);

    return Optional.ofNullable(request)
        .map(NaverUserCreateServiceRequest::toNaverUserInfo)
        .orElseThrow(() -> new OAuth2AuthenticationException("존재하지 않는 네이버 사용자입니다."));
  }
}
