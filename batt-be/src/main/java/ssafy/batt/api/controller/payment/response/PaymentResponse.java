package ssafy.batt.api.controller.payment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

  private String orderId;
  
  private String totalAmount;
  
  private String status;
  
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private Timestamp requestTime;
}