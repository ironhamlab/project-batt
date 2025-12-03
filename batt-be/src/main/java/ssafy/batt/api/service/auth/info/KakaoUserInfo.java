package ssafy.batt.api.service.auth.info;

import static ssafy.batt.domain.member.ProviderType.*;
import static ssafy.batt.domain.member.Sex.MAN;
import static ssafy.batt.domain.member.Sex.WOMAN;

import org.apache.commons.lang3.StringUtils;
import ssafy.batt.domain.member.ProviderType;
import ssafy.batt.domain.member.Sex;

public record KakaoUserInfo(
    String sub,
    String name,
    String gender,
    String email,
    String birthdate,
    String phoneNumber
) implements SocialUserInfo {

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public Sex getGender() {
    return StringUtils.equals(gender, "male") ? MAN : WOMAN;
  }

  @Override
  public String getBirth() {
    return birthdate;
  }

  @Override
  public String getProviderId() {
    return sub;
  }

  @Override
  public ProviderType getProviderType() {
    return KAKAO;
  }

  @Override
  public String getPhoneNumber() {
    String numberBody = phoneNumber.substring(4);
    return String.format("0%s", numberBody);
  }
}
