package com.fernandosantos.todosimple.Security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fernandosantos.todosimple.exceptions.GlobalExceptionHandler;
import com.fernandosantos.todosimple.models.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private JWTUtil jwtUtil;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        setAuthenticationFailureHandler(new GlobalExceptionHandler());
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            User userCredentials = new ObjectMapper().readValue(request.getInputStream(), User.class);

            System.out.println("Tentando autenticar usuário: " + userCredentials.getUsername());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userCredentials.getUsername(), userCredentials.getPassword(), new ArrayList<>());

            // Aqui, autenticamos o token
            Authentication authentication = this.authenticationManager.authenticate(authToken);
            return authentication;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar as credenciais: " + e.getMessage(), e);
        } catch (AuthenticationException e) {
            // Adicione uma mensagem de log mais clara
            System.err.println("Falha na autenticação: " + e.getMessage());
            throw new RuntimeException("Usuário inexistente ou senha inválida", e);
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
            throw new RuntimeException("Erro inesperado: " + e.getMessage(), e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain, Authentication authentication)
            throws IOException, ServletException {
        UserSpringSecurity userSpringSecurity = (UserSpringSecurity) authentication.getPrincipal();
        String username = userSpringSecurity.getUsername();
        String token = this.jwtUtil.generateToken(username);
        response.addHeader("Authorization", "Bearer " + token);
        response.addHeader("access-control-expose-headers", "Authorization");
    }

}