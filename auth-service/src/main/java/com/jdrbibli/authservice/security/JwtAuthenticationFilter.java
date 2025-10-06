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

/**
 * Filtre Spring Security exécuté une seule fois par requête.
 * 
 * Ce filtre intercepte chaque requête HTTP pour :
 * 1. Vérifier la présence d'un header "Authorization" avec un JWT.
 * 2. Extraire le pseudo de l'utilisateur depuis le JWT.
 * 3. Charger les informations de l'utilisateur via CustomUserDetailsService.
 * 4. Remplir le SecurityContext si le JWT est valide.
 * 
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService; // Service pour manipuler les JWT (extraction, validation)

    @Autowired
    private CustomUserDetailsService userDetailsService; // Service pour charger l'utilisateur

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Récupère le header Authorization
        final String authHeader = request.getHeader("Authorization");

        // Debug : affichage de tous les headers pour vérifier la requête
        request.getHeaderNames().asIterator()
                .forEachRemaining(h -> System.out.println(h + " = " + request.getHeader(h)));
        System.out.println("Authorization Header: " + authHeader);

        // Si le header est absent ou ne commence pas par "Bearer ", on continue la
        // chaîne sans auth
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Aucun token JWT trouvé dans le header");
            filterChain.doFilter(request, response);
            return;
        }

        // On récupère le token JWT (en retirant le préfixe "Bearer ")
        final String jwt = authHeader.substring(7);
        String pseudo;

        // Extraction du pseudo depuis le JWT
        try {
            pseudo = jwtService.extractPseudo(jwt);
            System.out.println("Pseudo extrait du token: " + pseudo);
        } catch (Exception e) {
            System.err.println("Erreur extraction pseudo du JWT: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // Si pseudo non null et qu'aucune authentification n'est présente dans le
        // contexte
        if (pseudo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Chargement de l'utilisateur depuis la base
                UserDetails userDetails = userDetailsService.loadUserByUsername(pseudo);

                // Vérification que le token est valide pour cet utilisateur
                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    // Création du token d'authentification Spring
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());

                    // Ajout des détails de la requête (IP, session, etc.)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Remplissage du contexte de sécurité avec l'utilisateur authentifié
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ AuthenticationContext rempli pour: " + pseudo);
                } else {
                    System.out.println("❌ Token JWT invalide pour: " + pseudo);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement UserDetails: " + e.getMessage());
            }
        }

        // On continue la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public void setUserDetailsService(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

}
