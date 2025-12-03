package ssafy.batt.api.service.payment;

import static java.time.LocalDate.*;
import static ssafy.batt.common.date.DateUtil.calculateDaysBetween;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.common.exception.payment.PaymentException;
import ssafy.batt.common.exception.payment.PaymentErrorCode;
import ssafy.batt.common.exception.performanceSchedule.PerformanceScheduleErrorCode;
import ssafy.batt.common.exception.performanceSchedule.PerformanceScheduleException;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.payment.Payment;
import ssafy.batt.domain.refund.Refund;
import ssafy.batt.domain.refund.RefundRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

  private final RefundRepository refundRepository;

  @Transactional
  public int processRefund(Payment payment, Integer cancelAmount, String reason) {
    double feeRate = calculateCancelFeeRate(payment.getBooking());

    if (!cancelAmount.equals(payment.getAmount())) {
      throw new PaymentException(PaymentErrorCode.CANCEL_AMOUNT_MISMATCH);
    }

    int feeAmount = (int) (cancelAmount * feeRate);
    int actualRefundAmount = cancelAmount - feeAmount;

    Refund refund = new Refund(payment, actualRefundAmount, feeAmount, reason);

    refund.completeRefund();

    refundRepository.save(refund);

    log.info("환불 처리 완료 - paymentId: {}, 원금: {}원, 수수료: {}원 ({}%), 실제환불: {}원",
        payment.getId(), cancelAmount,
        feeAmount, (int) (feeRate * 100), actualRefundAmount);
    
    return feeAmount;
  }

  private double calculateCancelFeeRate(Booking booking) {

    LocalDate today = now();
    LocalDate performanceDate = booking.getPerformanceSchedule().getPerformanceDate();
    long remainDays = calculateDaysBetween(today, performanceDate);

    if (remainDays == 0) {
      throw new PerformanceScheduleException(PerformanceScheduleErrorCode.SAME_DAY_CANCEL_NOT_ALLOWED);
    }

    if (remainDays <= 3) {
      return 0.3;
    } else if (remainDays <= 5) {
      return 0.2;
    } else if (remainDays <= 7) {
      return 0.1;
    } else {
      return 0.0;
    }
  }
}