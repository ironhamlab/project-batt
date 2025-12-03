package ssafy.batt.api.service.bid;

import static ssafy.batt.common.exception.transfer.TransferErrorCode.TRANSFER_NOT_FOUND;
import static ssafy.batt.common.validate.bid.BidValidator.validateBidAlreadyExists;
import static ssafy.batt.common.validate.bid.BidValidator.validateBidPrice;
import static ssafy.batt.common.validate.bid.BidValidator.validateBidderIsNotTransferOwner;
import static ssafy.batt.common.validate.transfer.TransferValidator.validateTransferStatus;
import static ssafy.batt.domain.coin.TransactionType.BID_FAIL;
import static ssafy.batt.domain.coin.TransactionType.BID_SUCCESS;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.api.controller.coin.response.RemainCoinResponse;
import ssafy.batt.api.service.bid.request.BidCreateServiceRequest;
import ssafy.batt.api.service.coin.CoinService;
import ssafy.batt.api.service.transfer.TransferSseService;
import ssafy.batt.api.service.transfer.info.TransferParticipantInfo;
import ssafy.batt.api.service.transfer.response.TransferDetailResponse;
import ssafy.batt.common.exception.transfer.TransferException;
import ssafy.batt.domain.bid.Bid;
import ssafy.batt.domain.bid.BidRepository;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.member.MemberRepository;
import ssafy.batt.domain.transfer.Transfer;
import ssafy.batt.domain.transfer.TransferRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BidService {

  private final CoinService coinService;
  private final BidRepository bidRepository;
  private final MemberRepository memberRepository;
  private final TransferRepository transferRepository;
  private final TransferSseService transferSseService;

  @Transactional
  public RemainCoinResponse createBid(BidCreateServiceRequest request, Member bidder, Long transferId) {

    Transfer transfer = transferRepository.findById(transferId).orElseThrow(
        () -> new TransferException(TRANSFER_NOT_FOUND)
    );

    validateTransferStatus(transfer.getStatus());
    validateBidPrice(request.coin(), transfer.getCurrentHighestBid());

    bidRepository
        .findFirstByTransferIdOrderByBidAmountDesc(transferId).ifPresent(
            currentHighestBid -> {
              Member currentHighestBidder = memberRepository.findByEmail(currentHighestBid.getBidder().getEmail());

              validateBidderIsNotTransferOwner(bidder.getEmail(), transfer.getSeller().getEmail());
              validateBidAlreadyExists(bidder.getEmail(), currentHighestBid.getBidder().getEmail());
              coinService.updateCoinTransaction(currentHighestBidder, currentHighestBid.getBidAmount(), BID_FAIL);
            }
        );

    bidRepository.save(Bid.of(transfer, bidder, request.coin()));
    coinService.updateCoinTransaction(bidder, request.coin(), BID_SUCCESS);
    transfer.updateHighestBid(request.coin());

    TransferParticipantInfo participantInfo = new TransferParticipantInfo(transfer.getSeller().getId(), bidder.getId());
    Long performanceId = transfer.getBooking().getPerformanceSchedule().getPerformance().getId();
    TransferDetailResponse transferDetailResponse = TransferDetailResponse.from(transfer, transfer.getBooking(), participantInfo);
    transferSseService.sendUpdate(performanceId, transferDetailResponse);

    return new RemainCoinResponse(bidder.getCoinBalance());
  }
}