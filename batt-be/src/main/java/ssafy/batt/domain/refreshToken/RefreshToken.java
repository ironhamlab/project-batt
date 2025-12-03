package ssafy.batt.domain.refreshToken;

import static ssafy.batt.common.constant.ConstantUtil.REFRESH_TOKEN_EXPIRE_TIME;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "memberId", timeToLive = REFRESH_TOKEN_EXPIRE_TIME / 1_000)
public class RefreshToken {

  @Id
  @Indexed
  private Long memberId;

  private String token;

  private RefreshToken(Long memberId, String token) {
    this.memberId = memberId;
    this.token = token;
  }

  public static RefreshToken of(Long memberId, String token) {
    return new RefreshToken(memberId, token);
  }
}