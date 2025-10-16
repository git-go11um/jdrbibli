package com.jdrbibli.userservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test unitaire pour {@link StorageProperties}.
 */
@SpringBootTest(classes = TestConfig.class)
@TestPropertySource(properties = {
        "user.avatar.uploadDir=/tmp/uploads"
})
class StoragePropertiesTest {

    @Autowired
    private StorageProperties storageProperties;

    /**
     * Vérifie que la propriété configurée est bien injectée depuis la configuration de test.
     */
    @Test
    void shouldLoadUploadDirFromConfiguration() {
        assertThat(storageProperties.getUploadDir()).isEqualTo("/tmp/uploads");
    }

    /**
     * Vérifie que les getters/setters fonctionnent correctement indépendamment de Spring.
     */
    @Test
    void shouldSetAndGetUploadDirManually() {
        StorageProperties props = new StorageProperties();
        props.setUploadDir("/data/avatars");
        assertThat(props.getUploadDir()).isEqualTo("/data/avatars");
    }
}
