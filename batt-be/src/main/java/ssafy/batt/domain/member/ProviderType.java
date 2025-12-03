package ssafy.batt.domain.member;

import static ssafy.batt.common.exception.auth.AuthErrorCode.UNSUPPORTED_PROVIDER;

import java.util.Arrays;
import ssafy.batt.common.exception.auth.AuthException;

public enum ProviderType {
  KAKAO, NAVER;

  public static ProviderType convert(String registrationId) {

    return Arrays.stream(ProviderType.values())
        .filter(providerType -> providerType.name().equalsIgnoreCase(registrationId))
        .findFirst()
        .orElseThrow(() -> new AuthException(UNSUPPORTED_PROVIDER));
  }
}