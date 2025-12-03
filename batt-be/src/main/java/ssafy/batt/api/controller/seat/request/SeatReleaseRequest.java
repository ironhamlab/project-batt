package ssafy.batt.api.controller.seat.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeatReleaseRequest {

  private List<Long> seatIds;
  
}