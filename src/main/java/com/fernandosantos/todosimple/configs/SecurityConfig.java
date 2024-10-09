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

        private static final String[] PUBLIC_MATCHERS = { "/", "/public/**" }; // Rota pública
        private static final String[] PUBLIC_MATCHERS_POST = { "/user", "/login" }; // Rotas públicas para POST

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configura CORS
                                .csrf(csrf -> csrf.disable()) // Desativa CSRF
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll() // Permite
                                                                                                                    // POST
                                                                                                                    // nas
                                                                                                                    // rotas
                                                                                                                    // públicas
                                                .requestMatchers(PUBLIC_MATCHERS).permitAll() // Permite acesso a rotas
                                                                                              // públicas
                                                .anyRequest().authenticated() // Exige autenticação para todas as outras
                                                                              // rotas
                                )
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Define a
                                                                                                        // política de
                                                                                                        // sessão
                                );

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
