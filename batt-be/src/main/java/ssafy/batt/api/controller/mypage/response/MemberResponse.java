package ssafy.batt.api.controller.mypage.response;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.member.ProviderType;
import ssafy.batt.domain.member.Sex;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class MemberResponse {

  private final Long id;

  private final String name;

  private final Sex sex;

  private String birth;

  private final String email;

  private final String phoneNumber;

  private final ProviderType providerType;

  public static MemberResponse from(Member member) {
    return new MemberResponse(
        member.getId(),
        member.getName(),
        member.getSex(),
        member.getBirth(),
        member.getEmail(),
        member.getPhoneNumber(),
        member.getProviderType()
    );
  }
}