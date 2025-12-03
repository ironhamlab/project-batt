package ssafy.batt.common.handler;

import static ssafy.batt.common.constant.ConstantUtil.ERROR_REDIRECT_URL;
import static ssafy.batt.common.exception.auth.AuthErrorCode.LOGIN_FAILED;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

  @Value("${host.frontend}")
  private String FRONT_SERVER;

  private final RedirectStrategy redirectStrategy;

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception
  ) throws IOException, ServletException {
    String redirectUri = determineTargetUrl(LOGIN_FAILED.toString());
    redirectStrategy.sendRedirect(request, response, redirectUri);
  }

  private String determineTargetUrl(String errorParam) {
    return UriComponentsBuilder
        .fromUriString(FRONT_SERVER + ERROR_REDIRECT_URL + errorParam)
        .build().toUriString();
  }
}

