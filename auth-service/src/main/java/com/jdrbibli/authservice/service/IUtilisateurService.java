package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.entity.Utilisateur;

public interface IUtilisateurService {
    Utilisateur inscrireNouvelUtilisateur(String pseudo, String email, String motDePasse);
}
