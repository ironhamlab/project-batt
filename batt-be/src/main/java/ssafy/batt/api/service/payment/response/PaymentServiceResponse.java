package ssafy.batt.api.service.payment.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentServiceResponse {

  private String orderId;
  
  private String totalAmount;
  
  private String status;
  
  private LocalDateTime requestTime;
}