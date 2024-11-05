package com.marciocosta.todosimple.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import com.marciocosta.todosimple.security.JWTAuthenticationFilter;
import com.marciocosta.todosimple.security.JWTUtil;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTUtil jwtUtil;
    
    private static final String[] PUBLIC_MATCHERS = {
        "/"
    };

    private static final String[] PUBLIC_MATCHERS_POST = {
        "/user",
        "/login"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable();

        AuthenticationManagerBuilder authenticationManagerBuilder = http
            .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(this.userDetailsService)
            .passwordEncoder(bCryptPasswordEncoder());
        this.authenticationManager = authenticationManagerBuilder.build();

        http.authorizeRequests()
            .requestMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll()
            .requestMatchers(PUBLIC_MATCHERS).permitAll()
            .anyRequest().authenticated().and()
            .authenticationManager(authenticationManager);

        http.addFilter(new JWTAuthenticationFilter(this.authenticationManager, this.jwtUtil));

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        
        // Define métodos permitidos
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
        
        // Define origens permitidas (caso precise permitir apenas algumas, defina aqui)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Exemplo de origem permitida
        
        // Define cabeçalhos permitidos
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        
        // Configuração de exposição de cabeçalhos (caso precise expor cabeçalhos específicos)
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return (CorsConfigurationSource) source;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
