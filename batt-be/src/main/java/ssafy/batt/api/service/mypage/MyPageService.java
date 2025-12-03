package ssafy.batt.api.service.mypage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.batt.api.controller.mypage.response.MemberResponse;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.member.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

  private final MemberRepository memberRepository;

  public MemberResponse getMyPageInfo(Long memberId) {
    return memberRepository.findById(memberId)
        .map(MemberResponse::from)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
  }

  @Transactional
  public void softDeleteMember(Long memberId) {
    Member findMember = memberRepository.findById(memberId).orElseThrow(
        () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
    );
    findMember.softDelete();
  }
}