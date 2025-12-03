package ssafy.batt.api.service.auth.info;

import static ssafy.batt.domain.member.ProviderType.NAVER;
import static ssafy.batt.domain.member.Sex.*;

import org.apache.commons.lang3.StringUtils;
import ssafy.batt.domain.member.ProviderType;
import ssafy.batt.domain.member.Sex;

public record NaverUserInfo(
    String id,
    String name,
    String email,
    String gender,
    String mobile,
    String birthday,
    String birthyear
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
    return StringUtils.equals(gender, "M") ? MAN : WOMAN;
  }

  @Override
  public String getBirth() {
    return String.format("%s-%s", birthyear, birthday);
  }

  @Override
  public String getProviderId() {
    return id;
  }

  @Override
  public String getPhoneNumber() {
    return mobile;
  }

  @Override
  public ProviderType getProviderType() {
    return NAVER;
  }
}
