package ssafy.batt.api.service.payment.request;

import ssafy.batt.api.controller.payment.request.PaymentRequest;

public record PaymentServiceRequest(
    String customerName,
    String customerPhone,
    String customerEmail,
    String eventName,
    String eventDate,
    String eventTime,
    String venue,
    int seatCount,
    String seatInfo,
    long totalAmount
) {

  public static PaymentServiceRequest from(PaymentRequest request) {
    return new PaymentServiceRequest(
        request.customerName(),
        request.customerPhone(),
        request.customerEmail(),
        request.eventName(),
        request.eventDate(),
        request.eventTime(),
        request.venue(),
        request.seatCount(),
        request.seatInfo(),
        request.totalAmount()
    );
  }
}