package ssafy.batt.api.controller.mypage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssafy.batt.api.controller.mypage.response.MemberResponse;
import ssafy.batt.api.service.mypage.MyPageService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@PreAuthorize("hasRole('USER')")
public class MyPageController {

  private final MyPageService myPageService;

  @GetMapping
  public ResponseEntity<MemberResponse> getMyPageInfo(
      @RequestParam Long memberId
  ) {
    MemberResponse memberResponse = myPageService.getMyPageInfo(memberId);
    return ResponseEntity.ok().body(memberResponse);
  }

  @PatchMapping("/{memberId}")
  public ResponseEntity<HttpStatus> softDeleteMember(
      @PathVariable Long memberId
  ) {
    myPageService.softDeleteMember(memberId);
    return ResponseEntity.ok().build();
  }
}
