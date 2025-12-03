package ssafy.batt.api.controller.payment.request;

import java.util.List;

public record PaymentSuccessRequest(
    Long userId,
    Long bookingId,
    Long performanceScheduleId,
    int seatCount,
    List<Long> seatIds,
    String paymentKey,
    String orderId,
    int amount
) {

}