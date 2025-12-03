package ssafy.batt.api.service.auth.request;

import static lombok.AccessLevel.PROTECTED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.batt.api.service.auth.info.NaverUserInfo;

@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class NaverUserCreateServiceRequest {

  private String resultCode;
  private String message;
  private Response response;

  @Getter
  @AllArgsConstructor(access = PROTECTED)
  public static class Response {

    private final String id;
    private final String name;
    private final String email;
    private final String gender;
    private final String birthday;
    private final String birthyear;
    private final String mobile;
  }

  public NaverUserInfo toNaverUserInfo() {
    return new NaverUserInfo(
        response.getId(),
        response.getName(),
        response.getEmail(),
        response.getGender(),
        response.getMobile(),
        response.getBirthday(),
        response.getBirthyear()
    );
  }

  public static NaverUserCreateServiceRequest of(
      String resultCode,
      String message,
      Response response
  ) {
    return new NaverUserCreateServiceRequest(
        resultCode,
        message,
        response
    );
  }
}
