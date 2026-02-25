package ar.com.lbr.precisionappbe.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // aplica a todos los endpoints
                        .allowedOrigins("*") // permite cualquier origen
                        .allowedMethods("*") // permite todos los métodos (GET, POST, etc.)
                        .allowedHeaders("*") // permite todos los headers
                        .allowCredentials(false); // sin cookies ni tokens si está en false
            }
        };
    }
}
