package com.marciocosta.todosimple.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marciocosta.todosimple.exceptions.GlobalExceptionHandler;
import com.marciocosta.todosimple.models.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{ //filtro exclusivo para autenticar login e senha.
    
    private AuthenticationManager authenticationManager;

    private JWTUtil jwtUtil;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        setAuthenticationFailureHandler(new GlobalExceptionHandler());
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        try {
            User userCredentials = new ObjectMapper().readValue(request.getInputStream(), User.class);
            System.out.println("Username: " + userCredentials.getUsername());  // Log temporário
            System.out.println("Password: " + userCredentials.getPassword());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userCredentials.getUsername(), userCredentials.getPassword(), new ArrayList<>());

            
            return this.authenticationManager.authenticate(authToken);
        } catch (Exception e) {
            throw new RuntimeException("Faleid to parse authetication request body", e);
        }
    }

    @Override
    protected void successfulAuthentication (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authentication) throws IOException, ServletException {
        UserSpringSecurity userSpringSecurity = (UserSpringSecurity) authentication.getPrincipal();
        String userName = userSpringSecurity.getUsername();
        String token = this.jwtUtil.generateToken(userName);
        response.addHeader("Authorization", "Bearer " + token);
        response.addHeader("access-control-expose-headers", "Authorization");
    }
}
