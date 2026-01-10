package com.leeyujun.stockinsightapi.common.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;


public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtToeknProvider;

    public JwtAuthFilter(JwtTokenProvider jwtToeknProvider) {
        this.jwtToeknProvider = jwtToeknProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);

            try{
                Jws<Claims> jws = Jwts.parser()
                        .verifyWith((javax.crypto.SecretKey) jwtToeknProvider.getKey())
                        .build()
                        .parseClaimsJws(token);

                Claims claims = jws.getBody();
                String userId = claims.getSubject();
                String role = String.valueOf(claims.get("role"));

                var aauthorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                var authentication = new UsernamePasswordAuthenticationToken(userId, null, aauthorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);


            }catch(Exception ignored){

                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
