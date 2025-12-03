package ssafy.batt.api.controller.payment.response;

import lombok.Getter;

@Getter
public class PaymentCancelResponse {

    private Long bookingId;
    private String message;

    public PaymentCancelResponse(Long bookingId, String message) {
        this.bookingId = bookingId;
        this.message = message;
    }
}