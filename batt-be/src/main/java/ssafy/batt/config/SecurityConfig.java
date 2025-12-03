package ssafy.batt.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ssafy.batt.api.service.auth.CustomOAuth2UserService;
import ssafy.batt.api.service.auth.CustomOidcUserService;
import ssafy.batt.common.filter.JwtAuthenticationFilter;
import ssafy.batt.common.handler.CustomAccessDeniedHandler;
import ssafy.batt.common.handler.CustomAuthenticationEntryPoint;
import ssafy.batt.common.handler.OAuth2AuthenticationFailureHandler;
import ssafy.batt.common.handler.OAuth2AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomOidcUserService customOidcUserService;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
  private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http)
      throws Exception {

    http
        .csrf(CsrfConfigurer::disable)
        .httpBasic(HttpBasicConfigurer::disable)
        .formLogin(FormLoginConfigurer::disable)
        .logout(LogoutConfigurer::disable);

    http
        .oauth2Client(withDefaults())
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo
                .oidcUserService(customOidcUserService)
                .userService(customOAuth2UserService)
            )
            .successHandler(oAuth2AuthenticationSuccessHandler)
            .failureHandler(oAuth2AuthenticationFailureHandler)
        );

    http
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    http
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler)
        );

    http.sessionManagement(
        session -> session.sessionCreationPolicy(STATELESS)
    );

    return http.build();
  }
}