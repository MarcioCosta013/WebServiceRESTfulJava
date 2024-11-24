package com.marciocosta.todosimple.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
// @EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**") // Todas as rotas
            .allowedOrigins("http://127.0.0.1:5500") // Origem do frontend
            .allowedMethods("GET", "PUT", "POST", "DELETE", "OPTIONS") // Métodos permitidos
            .allowedHeaders("*"); // Todos os cabeçalhos
    }
}
