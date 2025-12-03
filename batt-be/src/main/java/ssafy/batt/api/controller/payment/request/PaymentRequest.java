package ssafy.batt.api.controller.payment.request;

public record PaymentRequest(
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

}