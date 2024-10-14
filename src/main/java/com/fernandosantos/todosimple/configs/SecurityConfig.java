package com.fernandosantos.todosimple.configs;

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
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fernandosantos.todosimple.repositories.UserRepository;
import com.fernandosantos.todosimple.Security.JWTAuthenticationFilter;
import com.fernandosantos.todosimple.Security.JWTUtil;
import com.fernandosantos.todosimple.models.User;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private UserRepository userRepository; // Injetando o UserRepository

        @Autowired
        private JWTUtil jwtUtil;

        private static final String[] PUBLIC_MATCHERS = { "/" };
        private static final String[] PUBLIC_MATCHERS_POST = { "/user", "/login" };

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);
                authenticationManagerBuilder.userDetailsService(this.userDetailsService)
                                .passwordEncoder(bCryptPasswordEncoder());
                return authenticationManagerBuilder.build();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll()
                                                .requestMatchers(PUBLIC_MATCHERS).permitAll()
                                                .anyRequest().authenticated())
                                .authenticationManager(authenticationManager(http))
                                .addFilter(new JWTAuthenticationFilter(authenticationManager(http), jwtUtil))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
                configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
                final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        public User createUser(User user) {
                // Codifique a senha antes de salvar
                user.setPassword(bCryptPasswordEncoder().encode(user.getPassword()));
                return userRepository.save(user);
        }
}
