package com.jdrbibli.authservice.repository;

import com.jdrbibli.authservice.entity.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByRoleName_shouldReturnRoleIfExists() {
        Role role = new Role("ROLE_USER");
        roleRepository.save(role);

        Optional<Role> found = roleRepository.findByRoleName("ROLE_USER");

        assertThat(found).isPresent();
        assertThat(found.get().getRoleName()).isEqualTo("ROLE_USER");
    }

    @Test
    void findByRoleName_shouldReturnEmptyIfRoleDoesNotExist() {
        Optional<Role> found = roleRepository.findByRoleName("ROLE_ADMIN");
        assertThat(found).isEmpty();
    }

    @Test
    void save_shouldPersistRoleCorrectly() {
        Role role = new Role("ROLE_TEST");
        Role savedRole = roleRepository.save(role);

        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getRoleName()).isEqualTo("ROLE_TEST");
    }

    @Test
    void delete_shouldRemoveRole() {
        Role role = new Role("ROLE_DELETE");
        role = roleRepository.save(role);

        roleRepository.delete(role);

        Optional<Role> found = roleRepository.findByRoleName("ROLE_DELETE");
        assertThat(found).isEmpty();
    }
}
