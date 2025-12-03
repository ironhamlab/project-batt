package ssafy.batt.api.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ssafy.batt.api.controller.token.response.MobileJwtResponse;
import ssafy.batt.api.service.auth.info.KakaoUserInfo;
import ssafy.batt.api.service.auth.info.SocialUserInfo;
import ssafy.batt.api.service.member.MemberService;
import ssafy.batt.common.jwt.AccountContext;
import ssafy.batt.common.jwt.AuthToken;
import ssafy.batt.common.jwt.AuthTokenType;
import ssafy.batt.common.jwt.JwtTokenProvider;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.member.MemberRepository;

import java.util.List;
import java.util.Map;

import static ssafy.batt.common.jwt.AuthTokenType.ACCESS;
import static ssafy.batt.domain.member.Role.USER;
import static ssafy.batt.domain.member.Role.toAuthority;

@Service
@RequiredArgsConstructor
public class MobileSocialAuthService {

    private final KakaoApiService kakaoApiService;
    private final NaverApiService naverApiService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public MobileJwtResponse mobileSocialLogin(String provider, String accessToken) {
        SocialUserInfo userInfo;
        if ("kakao".equalsIgnoreCase(provider)) {
            userInfo = kakaoApiService.getKakaoUserInfoFromAccessToken(accessToken);
        } else if ("naver".equalsIgnoreCase(provider)) {
            userInfo = naverApiService.getNaverUserInfoFromAccessToken(accessToken);
        } else {
            throw new IllegalArgumentException("지원하지 않는 provider: " + provider);
        }

        Member member = memberService.registerMember(userInfo);
        AccountContext accountContext = AccountContext.of(member.getId(), List.of(new SimpleGrantedAuthority(toAuthority(USER))));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            accountContext, null, accountContext.getAuthorities()
        );
        AuthToken authToken = jwtTokenProvider.createToken(authentication, ACCESS);
      //        Member member = memberRepository.findById(accountContext.getMemberId())
//            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return new MobileJwtResponse(authToken.token(), member.getId());
    }

}
