package ssafy.batt.api.service.auth;


import static ssafy.batt.common.constant.ConstantUtil.BEARER_PREFIX;
import static ssafy.batt.common.constant.ConstantUtil.KAKAO_USER_INFO_URL;
import static ssafy.batt.common.constant.ConstantUtil.TOKEN_HEADER;

import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ssafy.batt.api.service.auth.info.KakaoUserInfo;
import ssafy.batt.api.service.auth.request.KakaoUserCreateServiceRequest;
import ssafy.batt.common.jwt.AccountContext;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

  private final RestClient restClient;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    OidcUser oidcUser = super.loadUser(userRequest);
    KakaoUserInfo userInfo = getKakaoUserInfo(userRequest);
    String registrationId = userRequest.getClientRegistration().getRegistrationId();

    return AccountContext.fromOidcUser(oidcUser, userInfo, registrationId);
  }

  private KakaoUserInfo getKakaoUserInfo(OidcUserRequest userRequest) {

    String accessToken = userRequest.getAccessToken().getTokenValue();
    KakaoUserCreateServiceRequest request = restClient.get()
        .uri(KAKAO_USER_INFO_URL)
        .header(TOKEN_HEADER, BEARER_PREFIX + accessToken)
        .retrieve()
        .body(KakaoUserCreateServiceRequest.class);

    return Optional.ofNullable(request)
        .map(KakaoUserCreateServiceRequest::toKakaoUserInfo)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
  }
}