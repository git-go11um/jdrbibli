package com.jdrbibli.ouvrage_service.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    // Constructeur de JwtAuthenticationFilter
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    // Méthode qui est appelée pour chaque requête
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        // Récupère le token JWT de la requête
        String token = getJwtFromRequest(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            // Si le token est valide, récupérer les claims
            Claims claims = jwtTokenProvider.getClaimsFromToken(token);
            String username = claims.getSubject();

            // Crée une authentification avec les informations de l'utilisateur
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Place l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    // Méthode pour extraire le token JWT de l'entête Authorization
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Retire "Bearer " du début du token
        }
        return null;
    }
}
