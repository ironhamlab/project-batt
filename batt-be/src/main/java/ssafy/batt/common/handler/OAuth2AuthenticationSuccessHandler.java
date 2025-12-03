package ssafy.batt.common.handler;

import static ssafy.batt.common.constant.ConstantUtil.COOKIE_NAME;
import static ssafy.batt.common.constant.ConstantUtil.LOGIN_REDIRECT_URL;
import static ssafy.batt.common.constant.ConstantUtil.REFRESH_TOKEN_EXPIRE_TIME;
import static ssafy.batt.common.jwt.AuthTokenType.REFRESH;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import ssafy.batt.api.service.member.MemberService;
import ssafy.batt.common.cookie.CookieUtil;
import ssafy.batt.common.jwt.AccountContext;
import ssafy.batt.common.jwt.AuthToken;
import ssafy.batt.common.jwt.JwtTokenProvider;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.refreshToken.RefreshToken;
import ssafy.batt.domain.refreshToken.RefreshTokenRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  @Value("${host.frontend}")
  private String FRONT_SERVER;

  private final CookieUtil cookieUtil;
  private final MemberService memberService;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedirectStrategy redirectStrategy;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {

    if (response.isCommitted()) {
      return;
    }

    AccountContext accountContext = (AccountContext) authentication.getPrincipal();

    Member member = memberService.registerMember(accountContext.getSocialUserInfo());
    accountContext.updateMemberInfo(member);

    setRefreshTokenCookie(authentication, response);
    String redirectUri = determineTargetUrl();

    redirectStrategy.sendRedirect(request, response, redirectUri);
  }

  private void setRefreshTokenCookie(Authentication authentication, HttpServletResponse response) {

    AccountContext accountContext = (AccountContext) authentication.getPrincipal();

    AuthToken refreshToken = jwtTokenProvider.createToken(authentication, REFRESH);
    refreshTokenRepository.save(RefreshToken.of(accountContext.getMemberId(), refreshToken.token()));
    cookieUtil.addCookie(response, COOKIE_NAME, refreshToken.token(), REFRESH_TOKEN_EXPIRE_TIME);
  }

  private String determineTargetUrl() {
    return UriComponentsBuilder
        .fromUriString(FRONT_SERVER + LOGIN_REDIRECT_URL)
        .build().toUriString();
  }
}