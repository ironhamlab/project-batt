package ssafy.batt.common.filter;

import static io.micrometer.common.util.StringUtils.isNotEmpty;
import static ssafy.batt.common.constant.ConstantUtil.BLACKLIST_KEY_PREFIX;
import static ssafy.batt.common.exception.auth.AuthErrorCode.TOKEN_EXPIRED;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ssafy.batt.common.exception.auth.AuthException;
import ssafy.batt.common.jwt.JwtTokenProvider;
import ssafy.batt.common.jwt.JwtTokenValidator;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtTokenValidator jwtTokenValidator;
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = jwtTokenValidator.resolveToken(request);

    if (isNotEmpty(token) && jwtTokenValidator.validateToken(token)) {
      if (redisTemplate.hasKey(BLACKLIST_KEY_PREFIX + token)) {
        throw new AuthException(TOKEN_EXPIRED);
      }
      Authentication authentication = jwtTokenValidator.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }
}
