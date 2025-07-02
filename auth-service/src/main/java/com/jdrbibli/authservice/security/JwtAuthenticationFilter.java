package com.jdrbibli.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String pseudo;

        // Vérifier que le header Authorization existe et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraire le token brut
        jwt = authHeader.substring(7);

        // Extraire le pseudo du token
        pseudo = jwtService.extractPseudo(jwt);

        // Si pseudo non nul et pas encore authentifié dans le contexte
        if (pseudo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Charger les infos utilisateur depuis UserDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(pseudo);

            // Vérifier la validité du token avec pseudo
            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                // Créer l'authentication token et l'ajouter au contexte
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Passer au filtre suivant
        filterChain.doFilter(request, response);
    }
}
