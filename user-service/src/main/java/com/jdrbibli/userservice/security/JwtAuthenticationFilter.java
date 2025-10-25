package com.jdrbibli.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filtre JWT qui intercepte chaque requête HTTP et valide le token dans
 * l'en-tête Authorization.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        log.info("PATH_RECU_JWT_FILTER: {} [{}]", request.getRequestURI(), request.getMethod());
        log.info("🆕 JwtAuthenticationFilter version REBUILT active !");

        String path = request.getRequestURI();
        log.debug("🚦 JwtAuthenticationFilter intercepts {}", path);

        // ✅ Bypass uniquement pour les routes réellement publiques
        if (path.startsWith("/auth")
                || path.startsWith("/api/auth")
                || path.startsWith("/actuator")
                || path.startsWith("/test")
                || (path.equals("/users") && "POST".equals(request.getMethod()))
                || (path.equals("/api/users") && "POST".equals(request.getMethod()))) {
            log.info("PUBLIC_ROUTE: accès public détecté pour {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        // 🔒 Vérifie le header Authorization
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Aucun header Authorization valide pour {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        String pseudo = null;

        try {
            pseudo = jwtService.extractPseudo(token);
            log.debug("🔐 Token reçu pour pseudo: {}", pseudo);

            if (pseudo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.isTokenValid(token, pseudo)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(pseudo,
                            null, null);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("✅ Authentification configurée pour {}", pseudo);
                } else {
                    log.warn("❌ Token invalide pour {}", pseudo);
                }
            }
        } catch (Exception e) {
            log.error("Erreur dans JwtAuthenticationFilter : {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
