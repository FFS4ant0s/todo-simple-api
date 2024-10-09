package com.fernandosantos.todosimple.configs;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final String[] PUBLIC_MARTCHERS = { "/" };

    private static final String[] PUBLIC_MARTCHERS_POST = { "/user", "/login" };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable()) // Desativa CORS
                .csrf(csrf -> csrf.disable()) // Desativa CSRF
                .authorizeHttpRequests(authorize -> authorize // Usando authorizeHttpRequests
                        .requestMatchers("/public/**").permitAll() // Permite acesso a rotas públicas
                        .anyRequest().authenticated() // Exige autenticação para todas as outras rotas
                );

        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated() // Autenticação para todas as requisições
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Definindo a política de sessão
                )
                .csrf(csrf -> csrf.disable());

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, PUBLIC_MARTCHERS_POST).permitAll()
                        .requestMatchers(PUBLIC_MARTCHERS).permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
