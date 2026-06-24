package com.party.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/user/login", "/api/user/portal/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/transfer/in/apply").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/branch/all").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                .requestMatchers(
                    "/api/portal/transfer/**",
                    "/api/portal/fee/**",
                    "/api/portal/activity/**",
                    "/api/portal/report/**",
                    "/api/portal/volunteer/**",
                    "/api/portal/dashboard/**",
                    "/api/portal/info/**"
                ).hasRole("MEMBER")
                .requestMatchers("/api/portal/**").hasAnyRole("MEMBER", "APPLICANT")
                .requestMatchers(HttpMethod.GET, "/api/development-report/**")
                    .hasAnyRole("ADMIN", "MEMBER", "APPLICANT")
                .requestMatchers(HttpMethod.POST, "/api/development-report")
                    .hasAnyRole("ADMIN", "MEMBER", "APPLICANT")
                .requestMatchers(HttpMethod.GET, "/api/evaluation/member/**")
                    .hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers(HttpMethod.GET, "/api/notification/list")
                    .hasAnyRole("ADMIN", "MEMBER", "APPLICANT")
                .requestMatchers(HttpMethod.PUT, "/api/report/task/update-expired")
                    .hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers("/api/ai/**").hasAnyRole("ADMIN", "MEMBER", "APPLICANT")
                .requestMatchers("/api/user/info", "/api/user/logout").authenticated()
                .anyRequest().hasRole("ADMIN")
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
