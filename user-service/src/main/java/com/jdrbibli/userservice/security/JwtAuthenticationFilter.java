package com.jdrbibli.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        System.out.println("Header Authorization reçu : " + authHeader);

        // 🔹 Debug : afficher secret et clé utilisée côté user-service
        System.out.println("Secret JWT côté user-service : " + jwtService.getJwtSecret());
        System.out.println("Clé signing côté user-service : " + jwtService.getSigningKeyForDebug());

        final String jwt;
        final String pseudo;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        System.out.println("Token reçu : " + jwt);

        try {
            pseudo = jwtService.extractPseudo(jwt);
            System.out.println("Pseudo extrait du token : " + pseudo);

            if (pseudo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = new User(pseudo, "", Collections.emptyList());

                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            // 🔹 Affiche l'erreur si le token est invalide
            System.err.println("Erreur JWT : " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

}
