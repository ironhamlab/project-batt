package ssafy.batt.api.service.auth.info;

import ssafy.batt.domain.member.ProviderType;
import ssafy.batt.domain.member.Sex;

public interface SocialUserInfo {

  String getName();

  Sex getGender();

  String getEmail();

  String getBirth();

  String getProviderId();

  ProviderType getProviderType();

  String getPhoneNumber();

}