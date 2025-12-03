package ssafy.batt.common.handler;

import static ssafy.batt.common.exception.member.MemberErrorCode.MEMBER_NOT_AUTHORIZED;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ssafy.batt.common.exception.member.MemberException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
      throws IOException, ServletException {
    throw new MemberException(MEMBER_NOT_AUTHORIZED);
  }
}
