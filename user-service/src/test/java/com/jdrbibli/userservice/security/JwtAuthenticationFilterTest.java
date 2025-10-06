package com.jdrbibli.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private JwtAuthenticationFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        filter = new JwtAuthenticationFilter(jwtService);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSkipFilterForUserCreation() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getMethod()).thenReturn("POST");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldSkipFilterIfNoAuthHeader() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldAuthenticateValidToken() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtService.extractPseudo("validToken")).thenReturn("user1");
        when(jwtService.isTokenValid("validToken", "user1")).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user1", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipAuthenticationIfTokenInvalid() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn("Bearer badToken");
        when(jwtService.extractPseudo("badToken")).thenReturn("user1");
        when(jwtService.isTokenValid("badToken", "user1")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipIfExtractThrows() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/other");
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractPseudo("token")).thenThrow(new RuntimeException("bad token"));

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
