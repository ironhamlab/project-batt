package ssafy.batt.api.service.seat;

import java.time.Duration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatRedisService {

  private final RedisTemplate<String, Object> redisTemplate;
  
  private static final String SEAT_HOLD_PREFIX = "seat:hold:";
  private static final Duration HOLD_DURATION = Duration.ofMinutes(1);

  public boolean holdSeat(Long seatId, Long performanceScheduleId, Long userId) {
    String key = SEAT_HOLD_PREFIX + performanceScheduleId + ":" + seatId;
    
    Boolean success = redisTemplate.opsForValue()
        .setIfAbsent(key, userId, HOLD_DURATION);
    
    return Boolean.TRUE.equals(success);
  }

  public void releaseSeat(Long seatId, Long performanceScheduleId) {
    String key = SEAT_HOLD_PREFIX + performanceScheduleId + ":" + seatId;
    redisTemplate.delete(key);
  }

  public Long getHoldingUser(Long seatId, Long performanceScheduleId) {
    String key = SEAT_HOLD_PREFIX + performanceScheduleId + ":" + seatId;
    Object userId = redisTemplate.opsForValue().get(key);
    
    return userId != null ? Long.valueOf(userId.toString()) : null;
  }

  public boolean isHeldBySameUser(Long seatId, Long performanceScheduleId, Long userId) {
    Long holdingUserId = getHoldingUser(seatId, performanceScheduleId);
    return holdingUserId != null && holdingUserId.equals(userId);
  }

  public void extendHold(Long seatId, Long performanceScheduleId, Long userId) {
    if (isHeldBySameUser(seatId, performanceScheduleId, userId)) {
      String key = SEAT_HOLD_PREFIX + performanceScheduleId + ":" + seatId;
      redisTemplate.expire(key, HOLD_DURATION);
    }
  }

  public Set<String> getAllHeldSeats() {
    return redisTemplate.keys(SEAT_HOLD_PREFIX + "*");
  }

  public Duration getHoldDuration() {
    return HOLD_DURATION;
  }

}