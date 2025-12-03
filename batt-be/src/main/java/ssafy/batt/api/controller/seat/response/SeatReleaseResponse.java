package ssafy.batt.api.controller.seat.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatReleaseResponse {

  private List<Long> releasedSeatIds;
  private List<Long> failedSeatIds;
  
}