package com.jdrbibli.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Reset SecurityContext before each test
        SecurityContextHolder.clearContext();

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_whenValidToken_thenSetsAuthentication() throws Exception {
        String pseudo = "testUser";
        String token = "Bearer validToken";

        request.addHeader("Authorization", token);

        when(jwtService.extractPseudo("validToken")).thenReturn(pseudo);
        UserDetails userDetails = new User(pseudo, "", new ArrayList<>());
        when(userDetailsService.loadUserByUsername(pseudo)).thenReturn(userDetails);
        when(jwtService.isTokenValid("validToken", pseudo)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should be set");
        assertEquals(pseudo,
                ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_whenInvalidToken_thenNoAuthentication() throws Exception {
        String invalidToken = "Bearer invalidToken";
        String pseudo = "testUser";

        request.addHeader("Authorization", invalidToken);

        when(jwtService.extractPseudo("invalidToken")).thenReturn(pseudo);
        UserDetails userDetails = User.withUsername(pseudo).password("pass").roles("USER").build();
        when(userDetailsService.loadUserByUsername(pseudo)).thenReturn(userDetails);
        when(jwtService.isTokenValid("invalidToken", pseudo)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should NOT be set");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_whenNoAuthorizationHeader_thenDoesNotSetAuthentication() throws Exception {
        // Pas de header Authorization
        // Par défaut, pas de header ajouté dans MockHttpServletRequest

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should NOT be set");
        verify(filterChain).doFilter(request, response);
    }
}
