package com.jdrbibli.authservice.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String pseudo;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, name = "mot_de_passe")
    private String motDePasse; // stocké hashé

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Constructeur sans arguments
    public User() {
    }

    // Constructeur avec tous les arguments
    public User(Long id, String pseudo, String email, String motDePasse, Set<Role> roles) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.motDePasse = motDePasse;
        this.roles = roles != null ? roles : new HashSet<>();
    }

    // Getters et setters

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

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles != null ? roles : new HashSet<>();
    }

    // Builder manuel simple (optionnel)
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String pseudo;
        private String email;
        private String motDePasse;
        private Set<Role> roles = new HashSet<>();

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

        public Builder motDePasse(String motDePasse) {
            this.motDePasse = motDePasse;
            return this;
        }

        public Builder roles(Set<Role> roles) {
            this.roles = roles != null ? roles : new HashSet<>();
            return this;
        }

        public User build() {
            return new User(id, pseudo, email, motDePasse, roles);
        }
    }
}
