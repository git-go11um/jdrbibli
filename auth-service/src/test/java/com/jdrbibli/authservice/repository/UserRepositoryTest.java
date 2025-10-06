/* package com.jdrbibli.authservice.repository;

import com.jdrbibli.authservice.entity.User;
import com.jdrbibli.authservice.jpa.TestJpaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
        // N'inclut aucune autre configuration par défaut
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class,
                org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
                org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.class,
                org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration.class
        }
)
@Import(TestJpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_shouldPersistUser() {
        User user = User.builder()
                .pseudo("user123")
                .email("user@test.com")
                .password("password")
                .build();

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        Optional<User> retrieved = userRepository.findByEmail("user@test.com");
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getPseudo()).isEqualTo("user123");
    }
}
 */