package ssafy.batt.api.controller.bid;

import static org.springframework.http.HttpStatus.OK;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ssafy.batt.api.controller.bid.request.BidCreateRequest;
import ssafy.batt.api.controller.coin.response.RemainCoinResponse;
import ssafy.batt.api.service.bid.BidService;
import ssafy.batt.api.service.bid.request.BidCreateServiceRequest;
import ssafy.batt.domain.member.Member;

@RestController
@RequiredArgsConstructor
public class BidController {

  private final BidService bidService;

  @PostMapping("/api/v1/transfers/{transferId}/bids")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<RemainCoinResponse> createdBid(
      Member member,
      @RequestBody BidCreateRequest request,
      @PathVariable Long transferId
  ) {
    RemainCoinResponse remainCoinResponse = bidService.createBid(BidCreateServiceRequest.from(request), member, transferId);
    return ResponseEntity.status(OK).body(remainCoinResponse);
  }
}