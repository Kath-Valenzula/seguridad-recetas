package com.demo.demo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.StringUtils;

import static com.demo.demo.Constants.BEARER_PREFIX;
import static com.demo.demo.Constants.getSigningKey;

import io.jsonwebtoken.Jwts;

@Configuration
public class JWTAuthenticationConfig {

    private final String jwtSecret;

    public JWTAuthenticationConfig(@Value("${security.jwt.secret:${JWT_SECRET:}}") String jwtSecret) {
        if (!StringUtils.hasText(jwtSecret)) {
            throw new IllegalStateException(
                    "JWT secret no configurado. Define 'security.jwt.secret' o la variable de entorno JWT_SECRET");
        }
        this.jwtSecret = jwtSecret;
    }
    
    public String getJWTToken(String username){
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");


    Map<String, Object> claims = new HashMap<>();
    claims.put("authorities", grantedAuthorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));

        String token = Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 1440))
                .and()
            .signWith(getSigningKey(jwtSecret))
                .compact();

        return BEARER_PREFIX + token;
    }
}
