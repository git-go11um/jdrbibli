package com.jdrbibli.authservice.security;

import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String pseudo) throws UsernameNotFoundException {
        return userRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec le pseudo : " + pseudo));
    }
}
