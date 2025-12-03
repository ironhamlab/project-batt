package ssafy.batt.api.service.payment;

import static ssafy.batt.common.exception.booking.BookingErrorCode.BOOKING_NOT_FOUND;
import static ssafy.batt.common.exception.member.MemberErrorCode.MEMBER_NOT_FOUND;
import static ssafy.batt.common.exception.payment.PaymentErrorCode.PAYMENT_ALREADY_CANCELLED;
import static ssafy.batt.common.exception.payment.PaymentErrorCode.PAYMENT_CONFIRMATION_FAILED;
import static ssafy.batt.common.exception.payment.PaymentErrorCode.PAYMENT_NOT_FOUND;
import static ssafy.batt.common.snowflake.Snowflake.formattingReservationId;
import static ssafy.batt.domain.booking.BookingStatus.CONFIRMED;
import static ssafy.batt.domain.payment.Method.TOSS;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.api.controller.payment.request.PaymentCancelRequest;
import ssafy.batt.api.controller.payment.request.PaymentSuccessRequest;
import ssafy.batt.api.controller.payment.response.PaymentCancelResponse;
import ssafy.batt.api.controller.payment.response.PaymentSuccessResponse;
import ssafy.batt.common.exception.booking.BookingException;
import ssafy.batt.common.exception.member.MemberException;
import ssafy.batt.common.exception.payment.PaymentException;
import ssafy.batt.common.exception.performanceSchedule.PerformanceScheduleErrorCode;
import ssafy.batt.common.exception.performanceSchedule.PerformanceScheduleException;
import ssafy.batt.domain.booingSeat.BookingSeat;
import ssafy.batt.domain.booingSeat.BookingSeatRepository;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.booking.BookingRepository;
import ssafy.batt.domain.booking.PaymentMethod;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.member.MemberRepository;
import ssafy.batt.domain.payment.Payment;
import ssafy.batt.domain.payment.PaymentRepository;
import ssafy.batt.domain.payment.Status;
import ssafy.batt.domain.schedule.PerformanceSchedule;
import ssafy.batt.domain.schedule.PerformanceScheduleRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  private final BookingRepository bookingRepository;
  private final PaymentRepository paymentRepository;
  private final MemberRepository memberRepository;
  private final PerformanceScheduleRepository performanceScheduleRepository;
  private final BookingSeatRepository bookingSeatRepository;
  private final TossPaymentClient tossPaymentClient;
  private final SeatReservationService seatReservationService;
  private final RefundService refundService;

  @Transactional
  public PaymentSuccessResponse processPaymentSuccess(PaymentSuccessRequest request) {

    Optional<Payment> existingPayment = paymentRepository.findByPaymentKey(request.paymentKey());
    if (existingPayment.isPresent()) {
      log.info("이미 처리된 결제 - paymentKey: {}", request.paymentKey());
      Booking existingBooking = existingPayment.get().getBooking();
      List<String> existingBookingNumbers = bookingSeatRepository.findByBooking(existingBooking)
          .stream()
          .map(BookingSeat::getBookingNumber)
          .toList();

      return new PaymentSuccessResponse(
          existingBooking.getId(),
          existingBooking.getOrderId(),
          existingBookingNumbers,
          Status.COMPLETED
      );
    }

    boolean paymentConfirmed = tossPaymentClient.confirmPayment(
        request.paymentKey(),
        request.orderId(),
        request.amount()
    );

    if (!paymentConfirmed) {
      throw new PaymentException(PAYMENT_CONFIRMATION_FAILED);
    }

    seatReservationService.validateSeatAvailability(request.seatIds(), request.performanceScheduleId());

    Member member = memberRepository.findById(request.userId())
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

    PerformanceSchedule performanceSchedule = performanceScheduleRepository.findById(request.performanceScheduleId())
        .orElseThrow(() -> new PerformanceScheduleException(PerformanceScheduleErrorCode.PERFORMANCE_SCHEDULE_NOT_FOUND));

    Booking booking = createBooking(request, member, performanceSchedule);
    bookingRepository.save(booking);

    Payment payment = createPayment(request, booking);
    paymentRepository.save(payment);

    seatReservationService.reserveSeats(request, booking);

    List<String> bookingNumbers = booking.getBookingSeats().stream()
        .map(BookingSeat::getBookingNumber)
        .toList();

    log.info("결제 성공 처리 완료 - orderId: {}, bookingNumbers: {}", request.orderId(), bookingNumbers);

    return new PaymentSuccessResponse(
        booking.getId(),
        request.orderId(),
        bookingNumbers,
        Status.COMPLETED
    );
  }

  public PaymentSuccessResponse processAfterTransferPaymentSuccess(PaymentSuccessRequest request) {

    Booking bidderBooking = bookingRepository.findById(request.bookingId())
        .orElseThrow(() -> new BookingException(BOOKING_NOT_FOUND));
    bidderBooking.updateBookingStatus(CONFIRMED);
    bidderBooking.updateBookingOrderId();
    Booking savedBooking = bookingRepository.save(bidderBooking);

    List<BookingSeat> existingBookingSeats = bookingSeatRepository.findByBooking(bidderBooking);
    for (BookingSeat bookingSeat : existingBookingSeats) {
      String newBookingNumber = formattingReservationId();
      bookingSeat.updateBookingNumber(newBookingNumber);
    }
    bookingSeatRepository.saveAll(existingBookingSeats);

    Payment newPayment = createPayment(request, savedBooking);
    paymentRepository.save(newPayment);

    List<String> newBookingNumbers = bidderBooking.getBookingSeats().stream()
        .map(BookingSeat::getBookingNumber)
        .toList();

    return new PaymentSuccessResponse(
        bidderBooking.getId(),
        bidderBooking.getOrderId(),
        newBookingNumbers,
        Status.COMPLETED
    );
  }

  private Booking createBooking(PaymentSuccessRequest request, Member member, PerformanceSchedule performanceSchedule) {
    Booking booking = new Booking(
        request.orderId(),
        request.seatCount(),
        request.amount(),
        PaymentMethod.TOSS,
        request.paymentKey(),
        member,
        performanceSchedule
    );
    booking.updateBookingStatus(CONFIRMED);
    return booking;
  }

  private Payment createPayment(PaymentSuccessRequest request, Booking booking) {
    Payment payment = new Payment(
        booking,
        request.paymentKey(),
        request.amount(),
        TOSS
    );
    payment.completePayment();
    return payment;
  }

  @Transactional
  public PaymentCancelResponse cancelPayment(PaymentCancelRequest request) {

    Booking booking = bookingRepository.findById(request.getBookingId())
        .orElseThrow(() -> new BookingException(BOOKING_NOT_FOUND));

    Payment payment = paymentRepository.findByBooking(booking)
        .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));

    if (payment.isCancelled()) {
      throw new PaymentException(PAYMENT_ALREADY_CANCELLED);
    }

    int cancellationFee = refundService.processRefund(payment, request.getCancelAmount(), request.getCancelReason());

    payment.cancelPayment();

    seatReservationService.releaseSeats(booking);

    booking.cancelBooking(cancellationFee);

    log.info("결제 취소 처리 완료 - bookingId: {}", request.getBookingId());

    return new PaymentCancelResponse(request.getBookingId(), "결제가 성공적으로 취소되었습니다");
  }
}