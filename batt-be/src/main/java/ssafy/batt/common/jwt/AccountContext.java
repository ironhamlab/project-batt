package ssafy.batt.common.jwt;

import static ssafy.batt.domain.member.Role.USER;
import static ssafy.batt.domain.member.Role.toAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import ssafy.batt.api.service.auth.info.SocialUserInfo;
import ssafy.batt.domain.member.Member;

@Slf4j
@Builder
@Getter
public class AccountContext implements UserDetails, OAuth2User, OidcUser {

  private Long memberId;
  private String email;
  private String password;
  private List<SimpleGrantedAuthority> roles;

  private String oauth2Id;
  private OidcIdToken idToken;
  private OidcUserInfo oidcUserInfo;
  private Map<String, Object> attributes;

  private SocialUserInfo socialUserInfo;
  private String registrationId;

  public static AccountContext fromOAuth2User(
      OAuth2User oAuth2User,
      SocialUserInfo socialUserInfo,
      String registrationId
  ) {
    return AccountContext.builder()
        .oauth2Id(oAuth2User.getName())
        .attributes(oAuth2User.getAttributes())
        .socialUserInfo(socialUserInfo)
        .email(socialUserInfo.getEmail())
        .password(socialUserInfo.getEmail())
        .registrationId(registrationId)
        .roles(List.of(new SimpleGrantedAuthority(toAuthority(USER))))
        .build();
  }

  public static AccountContext fromOidcUser(
      OidcUser oidcUser,
      SocialUserInfo socialUserInfo,
      String registrationId
  ) {
    return AccountContext.builder()
        .oauth2Id(oidcUser.getName())
        .attributes(oidcUser.getAttributes())
        .idToken(oidcUser.getIdToken())
        .oidcUserInfo(oidcUser.getUserInfo())
        .socialUserInfo(socialUserInfo)
        .email(socialUserInfo.getEmail())
        .password(socialUserInfo.getEmail())
        .registrationId(registrationId)
        .roles(List.of(new SimpleGrantedAuthority(toAuthority(USER))))
        .build();
  }

  public static AccountContext of(Long memberId, List<SimpleGrantedAuthority> roles) {
    return AccountContext.builder()
        .memberId(memberId)
        .roles(roles)
        .build();
  }

  public void updateMemberInfo(Member member) {
    this.memberId = member.getId();
  }

  @Override
  public String getName() {
    return oauth2Id;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public Map<String, Object> getClaims() {
    return Map.of();
  }

  @Override
  public OidcUserInfo getUserInfo() {
    return (OidcUserInfo) socialUserInfo;
  }

  @Override
  public OidcIdToken getIdToken() {
    return idToken;
  }
}