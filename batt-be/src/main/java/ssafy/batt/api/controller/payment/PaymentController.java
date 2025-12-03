package ssafy.batt.api.controller.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssafy.batt.api.controller.payment.request.PaymentCancelRequest;
import ssafy.batt.api.controller.payment.request.PaymentSuccessRequest;
import ssafy.batt.api.controller.payment.response.PaymentCancelResponse;
import ssafy.batt.api.controller.payment.response.PaymentSuccessResponse;
import ssafy.batt.api.service.payment.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@PreAuthorize("hasRole('USER')")
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping("/success")
  public ResponseEntity<PaymentSuccessResponse> processPaymentSuccess(@RequestBody PaymentSuccessRequest request) {
    PaymentSuccessResponse response = request.bookingId() == null ?
        paymentService.processPaymentSuccess(request) :
        paymentService.processAfterTransferPaymentSuccess(request);
    return ResponseEntity.ok().body(response);
  }

  @PostMapping("/cancel")
  public ResponseEntity<PaymentCancelResponse> cancelPayment(@RequestBody PaymentCancelRequest request) {
    PaymentCancelResponse response = paymentService.cancelPayment(request);
    return ResponseEntity.ok().body(response);
  }
}