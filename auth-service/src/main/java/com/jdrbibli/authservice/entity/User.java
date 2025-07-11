package com.jdrbibli.authservice.entity;

import jakarta.persistence.*;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String pseudo;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, name = "mot_de_passe")
    private String password; // stocké hashé

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // --- Attributs pour la réinitialisation du mot de passe ---
    @Column(name = "reset_code")
    private String resetCode;

    @Column(name = "reset_password_code")
    private String resetPasswordCode;

    @Column(name = "reset_password_code_expiration")
    private Long resetPasswordCodeExpiration; // timestamp

    // --- Relation avec PasswordResetToken ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PasswordResetToken> resetTokens = new HashSet<>();

    // --- Constructeurs ---
    public User() {
    }

    public User(Long id, String pseudo, String email, String password, Set<Role> roles) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.roles = roles != null ? roles : new HashSet<>();
    }

    public User(Long id, String pseudo, String email, String password, Set<Role> roles,
            String resetPasswordCode, Long resetPasswordCodeExpiration) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.roles = roles != null ? roles : new HashSet<>();
        this.resetPasswordCode = resetPasswordCode;
        this.resetPasswordCodeExpiration = resetPasswordCodeExpiration;
    }

    // --- Getters et setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles != null ? roles : new HashSet<>();
    }

    public String getResetPasswordCode() {
        return resetPasswordCode;
    }

    public void setResetPasswordCode(String resetPasswordCode) {
        this.resetPasswordCode = resetPasswordCode;
    }

    public Long getResetPasswordCodeExpiration() {
        return resetPasswordCodeExpiration;
    }

    public void setResetPasswordCodeExpiration(Long resetPasswordCodeExpiration) {
        this.resetPasswordCodeExpiration = resetPasswordCodeExpiration;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public Set<PasswordResetToken> getResetTokens() {
        return resetTokens;
    }

    public void setResetTokens(Set<PasswordResetToken> resetTokens) {
        this.resetTokens = resetTokens;
    }

    // --- Implémentation UserDetails ---
    @Override
    public Collection<Role> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return pseudo;
    } // ou email si tu préfères

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // --- Builder pratique ---
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String pseudo;
        private String email;
        private String password;
        private Set<Role> roles = new HashSet<>();
        private String resetPasswordCode;
        private Long resetPasswordCodeExpiration;
        private Set<PasswordResetToken> resetTokens = new HashSet<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder pseudo(String pseudo) {
            this.pseudo = pseudo;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder roles(Set<Role> roles) {
            this.roles = roles != null ? roles : new HashSet<>();
            return this;
        }

        public Builder resetPasswordCode(String resetPasswordCode) {
            this.resetPasswordCode = resetPasswordCode;
            return this;
        }

        public Builder resetPasswordCodeExpiration(Long resetPasswordCodeExpiration) {
            this.resetPasswordCodeExpiration = resetPasswordCodeExpiration;
            return this;
        }

        public Builder resetTokens(Set<PasswordResetToken> resetTokens) {
            this.resetTokens = resetTokens != null ? resetTokens : new HashSet<>();
            return this;
        }

        public User build() {
            User user = new User(id, pseudo, email, password, roles, resetPasswordCode, resetPasswordCodeExpiration);
            user.setResetTokens(resetTokens);
            return user;
        }
    }
}
