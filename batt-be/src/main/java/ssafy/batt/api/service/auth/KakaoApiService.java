package ssafy.batt.api.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ssafy.batt.api.service.auth.info.KakaoUserInfo;
import ssafy.batt.api.service.auth.request.KakaoUserCreateServiceRequest;

import java.util.Optional;

import static ssafy.batt.common.constant.ConstantUtil.*;

@Service
@RequiredArgsConstructor
public class KakaoApiService {
  private final RestClient restClient;

  public KakaoUserInfo getKakaoUserInfoFromAccessToken(String accessToken) {

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
