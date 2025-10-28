package com.multi.adapters.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final AppProps props;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(AbstractHttpConfigurer::disable)
      .cors(Customizer.withDefaults())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/actuator/health").permitAll()
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .anyRequest().permitAll() //TODO: Swap to API-Key/JWT
      );
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    var cfg = new CorsConfiguration();
    cfg.addAllowedOrigin(props.getAllowedOrigins());
    cfg.addAllowedHeader("*");
    cfg.addAllowedMethod("*");
    cfg.setAllowCredentials(true);
    var src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", cfg);
    return src;
  }
}
