package com.jdrbibli.ouvrage_service.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Ici, tu peux injecter ton repository pour charger les utilisateurs depuis la
    // base de données
    // Par exemple : private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ici, tu récupères l'utilisateur dans la base de données ou ailleurs
        // Ex : UserEntity user = userRepository.findByUsername(username);

        // Pour l'exemple, on va créer un utilisateur en dur
        if ("admin".equals(username)) {
            return User.withUsername("admin")
                    .password("{noop}password") // {noop} signifie qu'on n'applique pas de cryptage ici
                    .roles("USER")
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
