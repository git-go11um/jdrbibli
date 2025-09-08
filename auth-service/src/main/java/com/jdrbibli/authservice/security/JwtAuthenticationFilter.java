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
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 🔹 Debug : afficher tous les headers
        request.getHeaderNames().asIterator()
                .forEachRemaining(h -> System.out.println(h + " = " + request.getHeader(h)));

        System.out.println("Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Aucun token JWT trouvé dans le header");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        String pseudo;

        try {
            pseudo = jwtService.extractPseudo(jwt);
            System.out.println("Pseudo extrait du token: " + pseudo);
        } catch (Exception e) {
            System.err.println("Erreur extraction pseudo du JWT: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (pseudo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(pseudo);

                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ AuthenticationContext rempli pour: " + pseudo);
                } else {
                    System.out.println("❌ Token JWT invalide pour: " + pseudo);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement UserDetails: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
