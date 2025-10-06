package com.jdrbibli.authservice.config;

import com.jdrbibli.authservice.security.CustomUserDetailsService;
import com.jdrbibli.authservice.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour {@link SecurityConfig}.
 */
@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManager authenticationManager;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(
                userDetailsService,
                jwtAuthenticationFilter,
                passwordEncoder);
    }

    @Test
    void testConstructor_ShouldInitializeDependencies() {
        SecurityConfig config = new SecurityConfig(
                userDetailsService,
                jwtAuthenticationFilter,
                passwordEncoder);
        assertNotNull(config);
    }

    @Test
    void testConstructor_ShouldStoreUserDetailsService() throws Exception {
        Field field = SecurityConfig.class.getDeclaredField("userDetailsService");
        field.setAccessible(true);
        assertSame(userDetailsService, field.get(securityConfig));
    }

    @Test
    void testConstructor_ShouldStoreJwtAuthenticationFilter() throws Exception {
        Field field = SecurityConfig.class.getDeclaredField("jwtAuthenticationFilter");
        field.setAccessible(true);
        assertSame(jwtAuthenticationFilter, field.get(securityConfig));
    }

    @Test
    void testConstructor_ShouldStorePasswordEncoder() throws Exception {
        Field field = SecurityConfig.class.getDeclaredField("passwordEncoder");
        field.setAccessible(true);
        assertSame(passwordEncoder, field.get(securityConfig));
    }

    @Test
    void testAuthenticationProvider_ShouldReturnDaoAuthenticationProvider() {
        AuthenticationProvider provider = securityConfig.authenticationProvider();
        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }

    @Test
    void testAuthenticationProvider_ShouldUseCustomUserDetailsService() throws Exception {
        DaoAuthenticationProvider daoProvider = (DaoAuthenticationProvider) securityConfig.authenticationProvider();

        Field userDetailsField = DaoAuthenticationProvider.class.getDeclaredField("userDetailsService");
        userDetailsField.setAccessible(true);
        Object storedService = userDetailsField.get(daoProvider);

        assertSame(userDetailsService, storedService);
    }

    @Test
    void testAuthenticationProvider_ShouldUsePasswordEncoder() throws Exception {
        DaoAuthenticationProvider daoProvider = (DaoAuthenticationProvider) securityConfig.authenticationProvider();

        Field passwordEncoderField = DaoAuthenticationProvider.class.getDeclaredField("passwordEncoder");
        passwordEncoderField.setAccessible(true);
        Object storedEncoder = passwordEncoderField.get(daoProvider);

        assertSame(passwordEncoder, storedEncoder);
    }

    @Test
    void testAuthenticationManager_ShouldReturnAuthenticationManager() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
        AuthenticationManager manager = securityConfig.authenticationManager(authenticationConfiguration);
        assertNotNull(manager);
        assertEquals(authenticationManager, manager);
        verify(authenticationConfiguration, times(1)).getAuthenticationManager();
    }

    @Test
    void testAuthenticationManager_ShouldThrowException_WhenConfigurationFails() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager())
                .thenThrow(new RuntimeException("Configuration error"));
        assertThrows(RuntimeException.class, () -> securityConfig.authenticationManager(authenticationConfiguration));
        verify(authenticationConfiguration, times(1)).getAuthenticationManager();
    }

    @Test
    void testAuthenticationManager_ShouldPropagateException() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager())
                .thenThrow(new Exception("Generic exception"));
        assertThrows(Exception.class, () -> securityConfig.authenticationManager(authenticationConfiguration));
        verify(authenticationConfiguration, times(1)).getAuthenticationManager();
    }

    @Test
    void testAuthenticationProvider_ShouldReturnNewInstanceEachTime() {
        AuthenticationProvider provider1 = securityConfig.authenticationProvider();
        AuthenticationProvider provider2 = securityConfig.authenticationProvider();
        assertNotNull(provider1);
        assertNotNull(provider2);
        assertNotSame(provider1, provider2);
    }

    @Test
    void testAuthenticationProvider_MultipleCalls_ShouldAlwaysReturnDaoAuthenticationProvider() {
        assertTrue(securityConfig.authenticationProvider() instanceof DaoAuthenticationProvider);
        assertTrue(securityConfig.authenticationProvider() instanceof DaoAuthenticationProvider);
        assertTrue(securityConfig.authenticationProvider() instanceof DaoAuthenticationProvider);
    }

    @Test
    void testAuthenticationManager_WithNullConfiguration_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> securityConfig.authenticationManager(null));
    }

    @Test
    void testAuthenticationProvider_ShouldConfigureBothUserDetailsServiceAndPasswordEncoder() throws Exception {
        DaoAuthenticationProvider daoProvider = (DaoAuthenticationProvider) securityConfig.authenticationProvider();

        Field userDetailsField = DaoAuthenticationProvider.class.getDeclaredField("userDetailsService");
        userDetailsField.setAccessible(true);
        Object storedService = userDetailsField.get(daoProvider);

        Field passwordEncoderField = DaoAuthenticationProvider.class.getDeclaredField("passwordEncoder");
        passwordEncoderField.setAccessible(true);
        Object storedEncoder = passwordEncoderField.get(daoProvider);

        assertSame(userDetailsService, storedService);
        assertSame(passwordEncoder, storedEncoder);
    }
}
