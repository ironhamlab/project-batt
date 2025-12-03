package ssafy.batt.api.service.auth;

import static ssafy.batt.common.constant.ConstantUtil.BEARER_PREFIX;
import static ssafy.batt.common.constant.ConstantUtil.NAVER_USER_INFO_URL;
import static ssafy.batt.common.constant.ConstantUtil.TOKEN_HEADER;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import ssafy.batt.api.service.auth.info.NaverUserInfo;
import ssafy.batt.api.service.auth.request.NaverUserCreateServiceRequest;
import ssafy.batt.common.jwt.AccountContext;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final RestClient restClient;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    NaverUserInfo userInfo = getNaverUserInfo(userRequest);
    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    return AccountContext.fromOAuth2User(oAuth2User, userInfo, registrationId);
  }

  private NaverUserInfo getNaverUserInfo(OAuth2UserRequest userRequest) {
    String accessToken = userRequest.getAccessToken().getTokenValue();

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