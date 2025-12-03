package ssafy.batt.api.controller.member;

import static org.springframework.http.HttpStatus.OK;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssafy.batt.api.service.member.MemberService;
import ssafy.batt.domain.member.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/logout")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<HttpStatus> logout(HttpServletRequest request, HttpServletResponse response, Member member) {
    memberService.logout(request, response, member);
    return ResponseEntity.status(OK).build();
  }
}