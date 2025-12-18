package com.demo.demo;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.demo.demo.Constants.BEARER_PREFIX;
import static com.demo.demo.Constants.HEADER_AUTHORIZATION;
import static com.demo.demo.Constants.getSigningKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final String jwtSecret;

    public JWTAuthorizationFilter(@Value("${security.jwt.secret:${JWT_SECRET:}}") String jwtSecret) {
        if (!StringUtils.hasText(jwtSecret)) {
        throw new IllegalStateException(
            "JWT secret no configurado. Define 'security.jwt.secret' o la variable de entorno JWT_SECRET");
        }
        this.jwtSecret = jwtSecret;
    }

        private Claims setSigningKey(HttpServletRequest request) {
            String jwtToken = request.
                    getHeader(HEADER_AUTHORIZATION ).
                    replace(BEARER_PREFIX, "");

                    return Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey(jwtSecret))
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();

        }

        private void setAuthentication(Claims claims) {

            List<?> authorities = claims.get("authorities", List.class);
            if (authorities == null) {
                SecurityContextHolder.clearContext();
                return;
            }
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                    authorities.stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList()));

            SecurityContextHolder.getContext().setAuthentication(auth);

        }

        private boolean isJWTValid(HttpServletRequest request, HttpServletResponse res) {
            String authenticationHeader = request.getHeader(HEADER_AUTHORIZATION);
            if (authenticationHeader == null || !authenticationHeader.startsWith(BEARER_PREFIX))
                return false;
            return true;
        }

        @Override
        protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request, @SuppressWarnings("null") HttpServletResponse response, @SuppressWarnings("null") FilterChain filterChain) throws ServletException, IOException {
            try {
                if (isJWTValid(request, response)) {
                    Claims claims = setSigningKey(request);
                    if (claims.get("authorities") != null) {
                        setAuthentication(claims);
                    } else {
                        SecurityContextHolder.clearContext();
                    }
                } else {
                    SecurityContextHolder.clearContext();
                }
                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
                return;
            }
        }

    }
