package ssafy.batt.common.handler;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static ssafy.batt.common.exception.member.MemberErrorCode.MEMBER_NOT_AUTHORIZED;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ssafy.batt.common.exception.ErrorResponse;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    response.setStatus(UNAUTHORIZED.value());
    response.setContentType("application/json;charset=UTF-8");

    ErrorResponse errorResponse = ErrorResponse.of(MEMBER_NOT_AUTHORIZED);
    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}
