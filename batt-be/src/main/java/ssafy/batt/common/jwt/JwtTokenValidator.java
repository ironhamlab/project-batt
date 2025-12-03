package ssafy.batt.common.jwt;

import static io.jsonwebtoken.io.Decoders.BASE64;
import static java.lang.System.currentTimeMillis;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static ssafy.batt.common.constant.ConstantUtil.BEARER_PREFIX;
import static ssafy.batt.common.exception.auth.AuthErrorCode.TOKEN_EXPIRED;
import static ssafy.batt.common.exception.auth.AuthErrorCode.TOKEN_INVALID;
import static ssafy.batt.common.exception.auth.AuthErrorCode.TOKEN_UNSUPPORTED;
import static ssafy.batt.common.exception.auth.AuthErrorCode.TOKEN_WRONG;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ssafy.batt.common.exception.BattException;
import ssafy.batt.common.exception.auth.AuthException;

@Component
public class JwtTokenValidator {

  private final Key key;

  public JwtTokenValidator(@Value("${jwt.secret}") String secretKey) {
    byte[] keyBytes = BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public String resolveToken(HttpServletRequest request) {
    String authHeader = request.getHeader(AUTHORIZATION);

    if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
      return authHeader.substring(7);
    }
    return null;
  }

  public Authentication getAuthentication(String token) throws JwtException {

    if (StringUtils.isEmpty(token)) {
      return null;
    }

    Claims claims = parseClaims(token);
    Long memberId = claims.get("memberId", Long.class);
    List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

    AccountContext principal = AccountContext.of(memberId, authorities);
    return new UsernamePasswordAuthenticationToken(principal, token, authorities);
  }

  private Claims parseClaims(String token) throws JwtException {
    return Jwts
        .parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
    List<String> authoritiesClaim = (List<String>) claims.get("authorities");
    return authoritiesClaim.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (SecurityException | SignatureException | MalformedJwtException e) {
      throw new AuthException(TOKEN_INVALID);
    } catch (ExpiredJwtException e) {
      throw new BattException(TOKEN_EXPIRED);
    } catch (UnsupportedJwtException e) {
      throw new BattException(TOKEN_UNSUPPORTED);
    } catch (IllegalArgumentException e) {
      throw new BattException(TOKEN_WRONG);
    }
  }

  public long calculateTokenRemainingTime(String token) {
    Claims claims = parseClaims(token);
    long expirationTime = claims.getExpiration().getTime();
    long currentTime = currentTimeMillis();
    return (expirationTime - currentTime) / 1_000;
  }
}