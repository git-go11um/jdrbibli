package com.jdrbibli.authservice.repository;

import com.jdrbibli.authservice.entity.PasswordResetToken;
import com.jdrbibli.authservice.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@DataJpaTest
class PasswordResetTokenRepositoryTest {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByToken_shouldReturnTokenIfExists() {
        User user = new User();
        user.setPseudo("user1");
        user.setEmail("user1@test.com");
        user.setPassword("password");
        userRepository.save(user);

        PasswordResetToken token = new PasswordResetToken("token123", LocalDateTime.now().plusHours(1), user);
        tokenRepository.save(token);

        Optional<PasswordResetToken> found = tokenRepository.findByToken("token123");
        assertThat(found).isPresent();
        assertThat(found.get().getToken()).isEqualTo("token123");
        assertThat(found.get().getUser().getPseudo()).isEqualTo("user1");
    }

    @Test
    void deleteByUserId_shouldRemoveAllTokensForUser() {
        User user = new User();
        user.setPseudo("user2");
        user.setEmail("user2@test.com");
        user.setPassword("password");
        userRepository.save(user);

        PasswordResetToken token1 = new PasswordResetToken("t1", LocalDateTime.now().plusHours(1), user);
        PasswordResetToken token2 = new PasswordResetToken("t2", LocalDateTime.now().plusHours(1), user);
        tokenRepository.save(token1);
        tokenRepository.save(token2);

        tokenRepository.deleteByUserId(user.getId());

        assertThat(tokenRepository.findByToken("t1")).isEmpty();
        assertThat(tokenRepository.findByToken("t2")).isEmpty();
    }
}
