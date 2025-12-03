package ssafy.batt.api.controller.payment.request;

import lombok.Getter;

@Getter
public class PaymentCancelRequest {

    private Long bookingId;
    private String cancelReason;
    private Integer cancelAmount;

    protected PaymentCancelRequest() {}

    public PaymentCancelRequest(Long bookingId, String cancelReason, Integer cancelAmount) {
        this.bookingId = bookingId;
        this.cancelReason = cancelReason;
        this.cancelAmount = cancelAmount;
    }
}