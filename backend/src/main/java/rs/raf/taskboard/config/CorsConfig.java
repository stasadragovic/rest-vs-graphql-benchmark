package rs.raf.taskboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public CorsConfig(@Value("${taskboard.cors.allowed-origins}") String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    // Ista pravila za oba API-ja, da se ne razlikuju ni po ovome
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        for (String path : new String[] {"/api/**", "/graphql"}) {
            registry.addMapping(path)
                    .allowedOrigins(allowedOrigins)
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                    .allowedHeaders("*");
        }
    }
}