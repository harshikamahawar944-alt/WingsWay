package com.jkshian.arms.security;


import com.jkshian.arms.config.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index",
                                "/index.html",
                                "/home",
                                "/login",
                                "/login.html",
                                "/registration",
                                "/registration.html",
                                "/register",
                                "/signup",
                                "/signup.html",
                                "/contact",
                                "/contact.html",
                                "/select",
                                "/select.html",
                                "/ticket",
                                "/ticket.html",
                                "/payment",
                                "/payment.html",
                                "/confirmation",
                                "/confirmation.html",
                                "/my-bookings",
                                "/my-bookings.html",
                                "/admin",
                                "/admin/",
                                "/dashboard",
                                "/dashboard.html",
                                "/admin/dashboard",
                                "/admin/dashboard.html",
                                "/admin/flights",
                                "/admin/flights.html",
                                "/admin/flights/new",
                                "/admin/newflight",
                                "/admin/newflight.html",
                                "/admin/bookings",
                                "/admin/bookings.html",
                                "/admin/users",
                                "/admin/users.html",
                                "/admin/UserDetails.html",
                                "/admin/users/new",
                                "/admin/user",
                                "/admin/user.html",
                                "/admin/panel",
                                "/admin/panel.html",
                                "/admin/notifications",
                                "/admin/notifications.html",
                                "/api/v1/auth/**",
                                "/css/**",
                                "/images/**",
                                "/js/**",
                                "/font/**"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
