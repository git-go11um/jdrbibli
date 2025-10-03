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

/**
 * Filtre Spring Security pour l'authentification JWT.
 * 
 * Ce filtre intercepte chaque requête HTTP et vérifie la présence d'un token JWT
 * dans l'en-tête "Authorization". Si le token est valide, il crée un objet d'authentification
 * et le place dans le contexte de sécurité Spring.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructeur du filtre JWT.
     *
     * @param jwtTokenProvider   le fournisseur de tokens JWT
     * @param authenticationManager le gestionnaire d'authentification Spring
     */
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Filtre la requête entrante et authentifie l'utilisateur si un JWT valide est présent.
     *
     * @param request     la requête HTTP
     * @param response    la réponse HTTP
     * @param filterChain la chaîne de filtres
     * @throws IOException      en cas d'erreur I/O
     * @throws ServletException en cas d'erreur de servlet
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        String token = getJwtFromRequest(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Claims claims = jwtTokenProvider.getClaimsFromToken(token);
            String username = claims.getSubject();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, null);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Récupère le JWT depuis l'en-tête "Authorization" de la requête.
     *
     * @param request la requête HTTP
     * @return le token JWT si présent, sinon null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
