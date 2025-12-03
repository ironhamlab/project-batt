package ssafy.batt.common.constant;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ConstantUtil {

  // Header Constants
  public static final String TOKEN_HEADER = "Authorization";

  // Bearer Token Prefix
  public static final String BEARER_PREFIX = "Bearer ";

  // Cookie Name
  public static final String COOKIE_NAME = "batt";

  // Login Redirects -> Success & Failure
  public static final String LOGIN_REDIRECT_URL = "/login/callback";
  public static final String ERROR_REDIRECT_URL = "/login?error=";

  // User Info URL
  public static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v1/oidc/userinfo";
  public static final String NAVER_USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";

  // JWT Token Expiration and Refresh Times
  public static final int ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 3; // 3 hours
  public static final int REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 3; // 3 days

  // BlackList Key Prefix
  public static final String BLACKLIST_KEY_PREFIX = "blacklist:";

  // Coin Transaction Points
  public static final int SIGN_UP_POINT = 10;
  public static final int REVIEW_POINT = 50;

}