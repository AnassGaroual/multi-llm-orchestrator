package com.multi.adapters.infra;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Application infra properties (CORS, etc.). */
@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "app.cors")
public class AppProps {
  private String allowedOrigins = "http://localhost:5173";
}
