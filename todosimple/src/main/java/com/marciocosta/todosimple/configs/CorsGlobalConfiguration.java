package com.marciocosta.todosimple.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@Configuration
public class CorsGlobalConfiguration {
    /*
     * Esse filtro é aplicado globalmente e garante que todas as rotas
     * respondam corretamente às requisições CORS.
     */
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("http://127.0.0.1:5500")); // Origem do frontend
        config.setAllowedMethods(Collections.singletonList("*")); // Permitir todos os métodos (POST, GET, etc.)
        config.setAllowedHeaders(Collections.singletonList("*")); // Permitir todos os cabeçalhos
        config.setAllowCredentials(true); // Permitir cookies/sessões, se necessário

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}

