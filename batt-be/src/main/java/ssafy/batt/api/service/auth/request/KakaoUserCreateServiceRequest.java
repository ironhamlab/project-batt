package ssafy.batt.api.service.auth.request;

import static lombok.AccessLevel.PROTECTED;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.api.service.auth.info.KakaoUserInfo;

@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class KakaoUserCreateServiceRequest {

  private String sub;

  private String name;

  private String gender;

  private String email;

  private String birthdate;

  @JsonProperty(value = "email_verified")
  private Boolean emailVerified;

  @JsonProperty(value = "phone_number")
  private String phoneNumber;

  @JsonProperty(value = "phone_number_verified")
  private Boolean phoneNumberVerified;


  public KakaoUserInfo toKakaoUserInfo() {
    return new KakaoUserInfo(sub, name, gender, email, birthdate, phoneNumber);
  }

  public static KakaoUserCreateServiceRequest of(
      String sub,
      String name,
      String email,
      String gender,
      String birthdate,
      String phoneNumber
  ) {
    KakaoUserCreateServiceRequest request = new KakaoUserCreateServiceRequest();
    request.sub = sub;
    request.name = name;
    request.email = email;
    request.gender = gender;
    request.birthdate = birthdate;
    request.phoneNumber = phoneNumber;
    return request;
  }
}
