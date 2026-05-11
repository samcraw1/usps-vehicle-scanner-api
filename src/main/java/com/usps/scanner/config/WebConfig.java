package com.usps.scanner.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration tells Spring "this class holds setup, not business logic."
// CORS = Cross-Origin Resource Sharing. Browsers block requests from one domain to another
// unless the target server says "yes, I allow it." This config opens that gate.
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")              // applies to all /api/* endpoints
                .allowedOrigins("*")                // allow any frontend domain (tighten in prod)
                .allowedMethods("GET", "POST", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);                      // browsers cache the CORS approval for 1hr
    }
}
