package ssafy.batt.api.controller.token;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssafy.batt.api.controller.token.request.MobileLoginRequest;
import ssafy.batt.api.controller.token.response.IdentifierResponse;
import ssafy.batt.api.controller.token.response.MobileJwtResponse;
import ssafy.batt.api.service.auth.MobileSocialAuthService;
import ssafy.batt.api.service.token.TokenService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/token")
public class TokenController {

  private final TokenService tokenService;
  private final MobileSocialAuthService mobileSocialAuthService;

  @PostMapping("/reissue")
  public ResponseEntity<IdentifierResponse> reissueAccessToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    IdentifierResponse identifierResponse = tokenService.reissueToken(request, response);
    return ResponseEntity.ok().body(identifierResponse);
  }
  @PostMapping("/mobile-login")
  public ResponseEntity<?> mobileLogin(@RequestBody MobileLoginRequest request) {

    return ResponseEntity.ok(mobileSocialAuthService.mobileSocialLogin(request.getProvider(), request.getAccessToken()));
  }
}