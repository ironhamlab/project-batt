package ssafy.batt.api.service.booking;

import static ssafy.batt.common.exception.booking.BookingErrorCode.BOOKING_NOT_FOUND;
import static ssafy.batt.common.exception.member.MemberErrorCode.MEMBER_NOT_FOUND;
import static ssafy.batt.domain.booking.BookingStatus.TRANSFERRED;
import static ssafy.batt.domain.booking.BookingStatus.TRANSFER_FAILED_AUTO_CANCELLED;
import static ssafy.batt.domain.coin.Coin.calculateTransferRewardPoint;
import static ssafy.batt.domain.coin.TransactionType.TRANSFER_FAIL;
import static ssafy.batt.domain.coin.TransactionType.TRANSFER_SUCCESS;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.api.controller.booking.response.BookingDetailResponse;
import ssafy.batt.api.controller.booking.response.BookingListResponse;
import ssafy.batt.api.controller.booking.response.BookingPageResponse;
import ssafy.batt.api.service.coin.CoinService;
import ssafy.batt.api.service.payment.RefundService;
import ssafy.batt.common.exception.booking.BookingException;
import ssafy.batt.common.exception.member.MemberException;
import ssafy.batt.domain.bid.Bid;
import ssafy.batt.domain.bid.BidRepository;
import ssafy.batt.domain.booingSeat.BookingSeat;
import ssafy.batt.domain.booingSeat.BookingSeatRepository;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.booking.BookingRepository;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.member.MemberRepository;
import ssafy.batt.domain.transfer.Transfer;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

  private final CoinService coinService;
  private final RefundService refundService;
  private final BidRepository bidRepository;
  private final MemberRepository memberRepository;
  private final BookingRepository bookingRepository;
  private final BookingSeatRepository bookingSeatRepository;

  public Object getMyPageBookingInfo(
      Optional<Integer> page,
      Optional<Integer> size,
      Member member,
      Long memberId
  ) {
    if (isPagination(page, size)) {
      Pageable pageable = PageRequest.of(page.get(), size.get());
      return BookingPageResponse.from(bookingRepository.getMyPageBookingsBy(pageable, memberId));
    } else {
      return BookingListResponse.from(bookingRepository.getMyPageBookingsBy(memberId));
    }
  }

  public BookingDetailResponse getBookingDetailInfo(Member member, Long memberId, Long bookingId) {

    Member findMember = memberRepository.findById(memberId)
        .orElseThrow(
            () -> new MemberException(MEMBER_NOT_FOUND)
        );

    Booking booking = bookingRepository.findByIdAndMemberId(bookingId, memberId).orElseThrow(
        () -> new BookingException(BOOKING_NOT_FOUND)
    );

    return BookingDetailResponse.of(findMember, booking);
  }

  @Transactional
  public void updateBookingDetailsAfterTransfer(Transfer transfer) {

    boolean hasBidInfo = bidRepository.existsByTransfer(transfer);
    Booking booking = bookingRepository.findById(transfer.getBooking().getId()).orElseThrow(
        () -> new BookingException(BOOKING_NOT_FOUND)
    );

    if (hasBidInfo) {
      booking.updateBookingStatus(TRANSFERRED);
      Bid highestBid = bidRepository.findFirstByTransferIdOrderByBidAmountDesc(transfer.getId())
          .orElseThrow(() -> new BookingException(BOOKING_NOT_FOUND));

      Member seller = memberRepository.findByEmail(transfer.getSeller().getEmail());
      Member highestBidder = memberRepository.findByEmail(highestBid.getBidder().getEmail());
      Booking savedBooking = bookingRepository.save(Booking.createBookingFromTransfer(booking, highestBidder));
      List<BookingSeat> bookingSeats = booking.getBookingSeats()
          .stream()
          .map(bookingSeat -> BookingSeat.of(
              savedBooking,
              bookingSeat.getSeat(),
              bookingSeat.getPrice())
          )
          .toList();

      coinService.updateCoinTransaction(seller, calculateTransferRewardPoint(highestBid.getBidAmount()), TRANSFER_SUCCESS);
      bookingSeatRepository.saveAll(bookingSeats);
    } else {
      booking.updateBookingStatus(TRANSFER_FAILED_AUTO_CANCELLED);
      refundService.processRefund(
          transfer.getBooking().getPayment(),
          transfer.getBooking().getTotalAmount(),
          TRANSFER_FAIL.getDescription()
      );
    }
  }

  private static boolean isPagination(Optional<Integer> page, Optional<Integer> size) {
    return page.isPresent() && size.isPresent();
  }
}