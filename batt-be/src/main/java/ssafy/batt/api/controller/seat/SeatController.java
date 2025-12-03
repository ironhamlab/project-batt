package ssafy.batt.api.controller.seat;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssafy.batt.api.controller.seat.request.SeatHoldRequest;
import ssafy.batt.api.controller.seat.request.SeatReleaseRequest;
import ssafy.batt.api.controller.seat.response.SeatHoldResponse;
import ssafy.batt.api.controller.seat.response.SeatReleaseResponse;
import ssafy.batt.api.controller.seat.response.SeatResponse;
import ssafy.batt.api.service.seat.SeatService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SeatController {

  private final SeatService seatService;

  @GetMapping("/performance-schedules/{performanceScheduleId}/seats")
  public ResponseEntity<List<SeatResponse>> getSeats(
      @PathVariable Long performanceScheduleId) {

    List<SeatResponse> seats = seatService.getSeatsByPerformanceScheduleId(performanceScheduleId);
    return ResponseEntity.ok().body(seats);
  }

  @PostMapping("/performance-schedules/{performanceScheduleId}/seats/hold/{memberId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<SeatHoldResponse> holdSeats(
      @PathVariable Long performanceScheduleId,
      @PathVariable Long memberId,
      @RequestBody SeatHoldRequest request) {

    SeatHoldResponse response = seatService.holdSeats(request.getSeatIds(), performanceScheduleId, memberId);
    return ResponseEntity.ok().body(response);
  }

  @PostMapping("/performance-schedules/{performanceScheduleId}/seats/release/{memberId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<SeatReleaseResponse> releaseSeats(
      @PathVariable Long performanceScheduleId,
      @PathVariable Long memberId,
      @RequestBody SeatReleaseRequest request) {

    SeatReleaseResponse response = seatService.releaseSeatHolds(request.getSeatIds(), performanceScheduleId, memberId);
    return ResponseEntity.ok().body(response);
  }
}