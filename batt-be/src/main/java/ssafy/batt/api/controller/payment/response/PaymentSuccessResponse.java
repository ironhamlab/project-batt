package ssafy.batt.api.controller.payment.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.domain.payment.Status;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessResponse {

  private Long bookingId;

  private String orderId;
  
  private List<String> bookingNumbers;
  
  private Status Status;
}