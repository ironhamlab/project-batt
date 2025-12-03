package ssafy.batt.api.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentClient {

  private final ObjectMapper objectMapper;
  
  private static final String TOSS_PAYMENTS_URL = "https://api.tosspayments.com/v1/payments/confirm";
  private static final String SECRET_KEY = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R:";
  
  public boolean confirmPayment(String paymentKey, String orderId, int amount) {
    try {
      String authHeader = "Basic " + Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
      
      PaymentConfirmRequest confirmRequest = new PaymentConfirmRequest(paymentKey, orderId, amount);
      String requestBody = objectMapper.writeValueAsString(confirmRequest);
      
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(TOSS_PAYMENTS_URL))
          .header("Authorization", authHeader)
          .header("Content-Type", "application/json")
          .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
          .build();
      
      HttpResponse<String> response = HttpClient.newHttpClient()
          .send(request, HttpResponse.BodyHandlers.ofString());
      
      log.info("TossPayments API Response: {}", response.body());

      if (response.statusCode() != 200) {
        String responseBody = response.body();
        if (responseBody.contains("S008") && responseBody.contains("기존 요청을 처리중입니다")) {
          log.warn("TossPayments 중복 처리 에러 발생 - paymentKey: {}, orderId: {}", paymentKey, orderId);

          return checkPaymentStatus(paymentKey);
        }
      }
      
      return response.statusCode() == 200;
      
    } catch (Exception e) {
      log.error("TossPayments API call failed", e);
      return false;
    }
  }
  
  private boolean checkPaymentStatus(String paymentKey) {
    try {
      String authHeader = "Basic " + Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
      String queryUrl = "https://api.tosspayments.com/v1/payments/" + paymentKey;
      
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(queryUrl))
          .header("Authorization", authHeader)
          .header("Content-Type", "application/json")
          .method("GET", HttpRequest.BodyPublishers.noBody())
          .build();
      
      HttpResponse<String> response = HttpClient.newHttpClient()
          .send(request, HttpResponse.BodyHandlers.ofString());
      
      log.info("TossPayments 결제 상태 조회 Response: {}", response.body());
      
      if (response.statusCode() == 200) {
        String responseBody = response.body();

        boolean isDone = responseBody.contains("\"status\":\"DONE\"");
        log.info("결제 상태 확인 - isDone: {}, response: {}", isDone, responseBody);
        return isDone;
      }
      
      return false;
      
    } catch (Exception e) {
      log.error("TossPayments 결제 상태 조회 실패", e);
      return false;
    }
  }
  
  private record PaymentConfirmRequest(String paymentKey, String orderId, int amount) {}
}