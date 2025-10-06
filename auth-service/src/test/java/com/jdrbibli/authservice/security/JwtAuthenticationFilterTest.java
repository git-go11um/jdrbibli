package com.jdrbibli.authservice.security;

import com.jdrbibli.authservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.FilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private CustomUserDetailsService userDetailsService;
    private JwtAuthenticationFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userDetailsService = mock(CustomUserDetailsService.class);
        filterChain = mock(FilterChain.class);

        filter = new JwtAuthenticationFilter();
        // ✅ Utilisation des setters au lieu d'accès direct
        filter.setJwtService(jwtService);
        filter.setUserDetailsService(userDetailsService);

        // Nettoyage du SecurityContext avant chaque test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldSkip_whenNoAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_shouldSkip_whenInvalidBearerToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "InvalidToken");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_shouldAuthenticate_whenValidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String pseudo = "testUser";
        when(jwtService.extractPseudo("valid-jwt")).thenReturn(pseudo);
        when(jwtService.isTokenValid("valid-jwt", pseudo)).thenReturn(true);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(pseudo);
        when(userDetails.getAuthorities()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(pseudo)).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(pseudo);
    }

    @Test
    void doFilterInternal_shouldSkip_whenTokenInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String pseudo = "testUser";
        when(jwtService.extractPseudo("invalid-jwt")).thenReturn(pseudo);
        when(jwtService.isTokenValid("invalid-jwt", pseudo)).thenReturn(false);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(pseudo);
        when(userDetailsService.loadUserByUsername(pseudo)).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
