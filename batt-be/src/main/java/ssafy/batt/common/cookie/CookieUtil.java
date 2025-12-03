package ssafy.batt.common.cookie;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

  @Value("${cookie.domain}")
  private String COOKIE_DOMAIN;

  @Value("${cookie.secure-mode}")
  private boolean SECURE_MODE;

  public Optional<Cookie> getCookie(HttpServletRequest request, String cookieName) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(cookieName)) {
          return Optional.of(cookie);
        }
      }
    }
    return Optional.empty();
  }

  public void addCookie(HttpServletResponse response, String cookieName, String cookieValue, int maxAge) {
    ResponseCookie cookie = ResponseCookie.from(cookieName, cookieValue)
        .path("/")
        .domain(COOKIE_DOMAIN)
        .secure(SECURE_MODE)
        .sameSite("Lax")
        .httpOnly(false)
        .maxAge(maxAge)
        .build();
    response.addHeader("Set-Cookie", cookie.toString());
  }

  public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
    Optional<Cookie> optionalCookie = getCookie(request, cookieName);
    if (optionalCookie.isPresent()) {
      Cookie cookie = optionalCookie.get();
      cookie.setValue("");
      cookie.setPath("/");
      cookie.setMaxAge(0);
      response.addCookie(cookie);
    }
  }
}