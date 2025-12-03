package ssafy.batt.api.service.member;

import static java.time.Duration.ofSeconds;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static ssafy.batt.common.constant.ConstantUtil.BEARER_PREFIX;
import static ssafy.batt.common.constant.ConstantUtil.BLACKLIST_KEY_PREFIX;
import static ssafy.batt.common.constant.ConstantUtil.COOKIE_NAME;
import static ssafy.batt.common.constant.ConstantUtil.SIGN_UP_POINT;
import static ssafy.batt.domain.coin.TransactionType.SIGN_UP;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.api.service.auth.info.SocialUserInfo;
import ssafy.batt.api.service.coin.CoinService;
import ssafy.batt.common.cookie.CookieUtil;
import ssafy.batt.common.jwt.JwtTokenValidator;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.member.MemberRepository;
import ssafy.batt.domain.refreshToken.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

  private final CookieUtil cookieUtil;
  private final JwtTokenValidator jwtTokenValidator;
  private final RedisTemplate<String, Object> redisTemplate;
  private final CoinService coinService;
  private final MemberRepository memberRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public Member registerMember(SocialUserInfo userInfo) {
    if (memberRepository.existsMemberByEmail(userInfo.getEmail())) {
      return memberRepository.findByEmail(userInfo.getEmail());
    }
    Member savedMember = memberRepository.save(Member.from(userInfo));
    coinService.updateCoinTransaction(savedMember, SIGN_UP_POINT, SIGN_UP);
    return savedMember;
  }

  @Transactional
  public void logout(HttpServletRequest request, HttpServletResponse response, Member member) {
    deleteCookie(request, response);
    addAccessTokenToBlacklist(request);
    removeRefreshToken(member);
  }

  private void deleteCookie(HttpServletRequest request, HttpServletResponse response) {
    cookieUtil.deleteCookie(request, response, COOKIE_NAME);
  }

  private void addAccessTokenToBlacklist(HttpServletRequest request) {
    String authHeader = request.getHeader(AUTHORIZATION);
    if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
      String accessToken = authHeader.substring(7);
      long remainingTime = jwtTokenValidator.calculateTokenRemainingTime(accessToken);
      redisTemplate.opsForValue().set(BLACKLIST_KEY_PREFIX + accessToken, "true", ofSeconds(remainingTime));
    }
  }

  private void removeRefreshToken(Member member) {
    refreshTokenRepository.findById(member.getId())
        .ifPresent(refreshTokenRepository::delete);
  }
}