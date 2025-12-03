package ssafy.batt.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ssafy.batt.common.resolver.ArgumentResolver;

@EnableWebMvc
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  @Value("${host.allowed-origins}")
  private String[] allowedOrigins;

  private final ArgumentResolver argumentResolver;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(argumentResolver);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/v1/**")
        .allowedOrigins(allowedOrigins)
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .exposedHeaders("Authorization", "Set-Cookie")
        .allowCredentials(true);
  }
}