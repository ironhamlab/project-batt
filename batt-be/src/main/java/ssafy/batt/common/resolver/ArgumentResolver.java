package ssafy.batt.common.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ssafy.batt.common.jwt.AccountContext;
import ssafy.batt.domain.member.Member;
import ssafy.batt.domain.member.MemberRepository;

@Component
@RequiredArgsConstructor
public class ArgumentResolver implements HandlerMethodArgumentResolver {

  private final MemberRepository memberRepository;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(Member.class);
  }

  @Override
  public Member resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory
  ) throws Exception {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !(authentication.getPrincipal() instanceof AccountContext accountContext)) {
      return null;
    }

    return memberRepository.findById(accountContext.getMemberId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
  }
}