package ssafy.batt.api.controller.seat.response;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatHoldResponse {

  private Long performanceScheduleId;
  private List<Long> heldSeatIds;
  private List<Long> failedSeatIds;
  private Instant holdExpiresAt;
  
}