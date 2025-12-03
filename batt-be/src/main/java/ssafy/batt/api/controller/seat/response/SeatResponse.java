package ssafy.batt.api.controller.seat.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ssafy.batt.domain.seat.Grade;
import ssafy.batt.domain.seat.Seat;

@Getter
@AllArgsConstructor
public class SeatResponse {

  private Long seatId;
  private String number;
  private Grade grade;
  private Integer price;
  private Integer x;
  private Integer y;
  private Boolean status;
  private Long heldByUserId;

  public static SeatResponse of(Seat seat, Boolean isAvailable, Long heldByUserId) {
    return new SeatResponse(
        seat.getId(),
        seat.generateFormattingSeatNumber(),
        seat.getGrade(),
        seat.getPrice(),
        seat.getX(),
        seat.getY(),
        isAvailable,
        heldByUserId
    );
  }
}