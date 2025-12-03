package ssafy.batt.api.service.token;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static ssafy.batt.common.constant.ConstantUtil.BEARER_PREFIX;
import static ssafy.batt.common.constant.ConstantUtil.COOKIE_NAME;
import static ssafy.batt.common.constant.ConstantUtil.REFRESH_TOKEN_EXPIRE_TIME;
import static ssafy.batt.common.exception.auth.AuthErrorCode.REFRESH_TOKEN_EXPIRED;
import static ssafy.batt.common.exception.auth.AuthErrorCode.REFRESH_TOKEN_NOT_EXIST_IN_COOKIE;
import static ssafy.batt.common.exception.auth.AuthErrorCode.TOKEN_NOT_MATCHED;
import static ssafy.batt.common.jwt.AuthTokenType.ACCESS;
import static ssafy.batt.common.jwt.AuthTokenType.REFRESH;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.api.controller.token.response.IdentifierResponse;
import ssafy.batt.common.cookie.CookieUtil;
import ssafy.batt.common.exception.auth.AuthException;
import ssafy.batt.common.jwt.AccountContext;
import ssafy.batt.common.jwt.AuthToken;
import ssafy.batt.common.jwt.JwtTokenProvider;
import ssafy.batt.common.jwt.JwtTokenValidator;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.member.MemberRepository;
import ssafy.batt.domain.refreshToken.RefreshToken;
import ssafy.batt.domain.refreshToken.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

  private final CookieUtil cookieUtil;
  private final MemberRepository memberRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtTokenValidator jwtTokenValidator;
  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public IdentifierResponse reissueToken(HttpServletRequest request, HttpServletResponse response) {

    String refreshTokenInCookie = cookieUtil.getCookie(request, COOKIE_NAME)
        .map(Cookie::getValue).orElseThrow(() -> new AuthException(REFRESH_TOKEN_NOT_EXIST_IN_COOKIE));

    Authentication authentication = jwtTokenValidator.getAuthentication(refreshTokenInCookie);
    AccountContext accountContext = (AccountContext) authentication.getPrincipal();

    Member member = memberRepository.findById(accountContext.getMemberId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

    validateRefreshToken(refreshTokenInCookie, accountContext);
    RefreshToken newRefreshToken = updateRefreshToken(accountContext, authentication);
    clearCookie(request, response, newRefreshToken);

    AuthToken accessToken = jwtTokenProvider.createToken(authentication, ACCESS);
    response.setHeader(AUTHORIZATION, BEARER_PREFIX + accessToken.token());

    return IdentifierResponse.of(member.getId(), member.getCoinBalance());
  }

  private void clearCookie(HttpServletRequest request, HttpServletResponse response, RefreshToken refreshToken) {
    cookieUtil.deleteCookie(request, response, COOKIE_NAME);
    cookieUtil.addCookie(response, COOKIE_NAME, refreshToken.getToken(), REFRESH_TOKEN_EXPIRE_TIME);
  }

  private RefreshToken updateRefreshToken(AccountContext accountContext, Authentication authentication) {

    Long memberId = accountContext.getMemberId();
    refreshTokenRepository.deleteById(memberId);
    AuthToken newRefreshToken = jwtTokenProvider.createToken(authentication, REFRESH);
    return refreshTokenRepository.save(RefreshToken.of(memberId, newRefreshToken.token()));
  }

  private void validateRefreshToken(String oldToken, AccountContext accountContext) {

    if (!jwtTokenValidator.validateToken(oldToken)) {
      throw new AuthException(REFRESH_TOKEN_EXPIRED);
    }

    Long memberId = accountContext.getMemberId();
    RefreshToken findToken = refreshTokenRepository.findById(memberId).orElseThrow(
        () -> new AuthException(REFRESH_TOKEN_EXPIRED)
    );

    if (!StringUtils.equals(oldToken, findToken.getToken())) {
      throw new AuthException(TOKEN_NOT_MATCHED);
    }
  }
}