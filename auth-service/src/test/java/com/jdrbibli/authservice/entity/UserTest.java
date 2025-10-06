package com.jdrbibli.authservice.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("ROLE_USER"));

        User user = new User(1L, "pseudo", "email@test.com", "password", roles, "code", 123456L);
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getPseudo()).isEqualTo("pseudo");
        assertThat(user.getEmail()).isEqualTo("email@test.com");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getRoles()).containsExactlyElementsOf(roles);
        assertThat(user.getResetPasswordCode()).isEqualTo("code");
        assertThat(user.getResetPasswordCodeExpiration()).isEqualTo(123456L);
        assertThat(user.getAuthorities()).containsExactlyElementsOf(roles);
        assertThat(user.getUsername()).isEqualTo("pseudo");
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void testDefaultConstructorAndSetters() {
        User user = new User();
        user.setId(2L);
        user.setPseudo("pseudo2");
        user.setEmail("email2@test.com");
        user.setPassword("pass2");
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("ROLE_ADMIN"));
        user.setRoles(roles);
        user.setResetPasswordCode("reset2");
        user.setResetPasswordCodeExpiration(654321L);

        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getPseudo()).isEqualTo("pseudo2");
        assertThat(user.getEmail()).isEqualTo("email2@test.com");
        assertThat(user.getPassword()).isEqualTo("pass2");
        assertThat(user.getRoles()).containsExactlyElementsOf(roles);
        assertThat(user.getResetPasswordCode()).isEqualTo("reset2");
        assertThat(user.getResetPasswordCodeExpiration()).isEqualTo(654321L);
    }

    @Test
    void testBuilder() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("ROLE_USER"));

        User user = User.builder()
                .id(3L)
                .pseudo("pseudo3")
                .email("email3@test.com")
                .password("password3")
                .roles(roles)
                .resetPasswordCode("code3")
                .resetPasswordCodeExpiration(999999L)
                .build();

        assertThat(user.getId()).isEqualTo(3L);
        assertThat(user.getPseudo()).isEqualTo("pseudo3");
        assertThat(user.getEmail()).isEqualTo("email3@test.com");
        assertThat(user.getPassword()).isEqualTo("password3");
        assertThat(user.getRoles()).containsExactlyElementsOf(roles);
        assertThat(user.getResetPasswordCode()).isEqualTo("code3");
        assertThat(user.getResetPasswordCodeExpiration()).isEqualTo(999999L);
    }

    @Test
    void testResetTokensSetterAndGetter() {
        User user = new User();
        Set<PasswordResetToken> tokens = new HashSet<>();
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("token123");
        tokens.add(token);

        user.setResetTokens(tokens);

        assertThat(user.getResetTokens()).containsExactlyElementsOf(tokens);
    }
}
