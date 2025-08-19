package com.jdrbibli.authservice;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordCheck {

    public static void main(String[] args) {
        // Mot de passe en clair que tu veux tester
        String motClair = "T@u123456789";

        // Hash récupéré depuis la BDD (champ mot_de_passe)
        String hashBDD = "$2a$10$GGn8lVukQOqMUIjpCKC49.b7Y/vuJGtrUfvZ46kYXeQoNClyH932K"; // remplace par le vrai hash

        // Vérification
        boolean matches = BCrypt.checkpw(motClair, hashBDD);

        System.out.println("Mot de passe correct ? " + matches);
    }
}
