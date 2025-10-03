package com.jdrbibli.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre Spring Security qui intercepte chaque requête HTTP et valide
 * le JWT présent dans l'en-tête Authorization.
 * 
 * <p>
 * Si le token est valide, il place une authentification dans le contexte
 * de sécurité Spring afin que l'utilisateur soit reconnu pour cette requête.
 * </p>
 * 
 * <p>
 * Les requêtes de création d'utilisateur (/api/users POST) sont exclues
 * du filtrage JWT afin de permettre l'inscription sans authentification.
 * </p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /**
     * Crée un filtre d'authentification JWT avec le service JWT fourni.
     *
     * @param jwtService le service utilisé pour valider et extraire les informations du token
     */
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Filtre la requête HTTP pour extraire et valider le JWT.
     * Si le token est valide, configure l'authentification dans le contexte Spring Security.
     *
     * @param request     la requête HTTP entrante
     * @param response    la réponse HTTP
     * @param filterChain la chaîne de filtres à continuer
     * @throws ServletException en cas d'erreur de servlet
     * @throws IOException      en cas d'erreur d'entrée/sortie
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.equals("/api/users") && request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String pseudo;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7);
        try {
            pseudo = jwtService.extractPseudo(token);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (pseudo != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtService.isTokenValid(token, pseudo)) {

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(pseudo, null,
                        null);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
