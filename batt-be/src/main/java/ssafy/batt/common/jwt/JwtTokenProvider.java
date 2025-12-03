package ssafy.batt.common.jwt;

import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.Header.TYPE;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static ssafy.batt.common.constant.ConstantUtil.ACCESS_TOKEN_EXPIRE_TIME;
import static ssafy.batt.common.constant.ConstantUtil.REFRESH_TOKEN_EXPIRE_TIME;
import static ssafy.batt.common.jwt.AuthTokenType.ACCESS;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final Key key;

  public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
    byte[] keyBytes = BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public AuthToken createToken(Authentication authentication, AuthTokenType tokenType) {
    AccountContext accountContext = (AccountContext) authentication.getPrincipal();

    Date now = new Date();
    int expiration = tokenType == ACCESS ? ACCESS_TOKEN_EXPIRE_TIME : REFRESH_TOKEN_EXPIRE_TIME;
    Date expiryDate = new Date(now.getTime() + expiration);
    String jwtToken = generateJwt(accountContext, now, expiryDate);

    return AuthToken.builder()
        .memberId(accountContext.getMemberId())
        .token(jwtToken)
        .expiresIn(expiration)
        .expiryDate(getLocalDateTime(expiryDate))
        .type(tokenType)
        .build();
  }

  private static String getLocalDateTime(Date expiryDate) {
    LocalDateTime localDateTime = LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return localDateTime.format(formatter);
  }

  private String generateJwt(AccountContext accountContext, Date now, Date expiryDate) {

    List<String> authorities = accountContext.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList();

    return Jwts.builder()
        .setHeaderParam(TYPE, JWT_TYPE)
        .setSubject("BATT")
        .claim("memberId", accountContext.getMemberId())
        .claim("authorities", authorities)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key, HS256)
        .compact();
  }
}