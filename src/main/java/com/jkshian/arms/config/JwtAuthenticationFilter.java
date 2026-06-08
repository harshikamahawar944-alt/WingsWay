package com.jkshian.arms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.JwtException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final List<String> tokenCandidates = resolveTokens(request);
        if (tokenCandidates.isEmpty()) {
            filterChain.doFilter(request,response);
            return;
        }

        boolean invalidTokenFound = false;
        for (String jwt : tokenCandidates) {
            try {
                final String userEmail = jwtService.extractUsername(jwt);
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    if (jwtService.isTokenValid(jwt, userDetails)){
                        UsernamePasswordAuthenticationToken autheToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        autheToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(autheToken);
                        break;
                    }
                }
            } catch (JwtException | IllegalArgumentException ex) {
                invalidTokenFound = true;
            }
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null && invalidTokenFound) {
            SecurityContextHolder.clearContext();
            expireJwtCookie(response);
        }
        filterChain.doFilter(request,response);
    }

    private List<String> resolveTokens(HttpServletRequest request) {
        List<String> tokens = new ArrayList<>();
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokens.add(authHeader.substring(7));
        }

        if (request.getCookies() == null) {
            return tokens;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("jwtToken".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                tokens.add(decodeCookieValue(cookie.getValue()));
            }
        }

        return tokens;
    }

    private String decodeCookieValue(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return value;
        }
    }

    private void expireJwtCookie(HttpServletResponse response) {
        Cookie expiredCookie = new Cookie("jwtToken", "");
        expiredCookie.setHttpOnly(true);
        expiredCookie.setPath("/");
        expiredCookie.setMaxAge(0);
        response.addCookie(expiredCookie);
    }
}
