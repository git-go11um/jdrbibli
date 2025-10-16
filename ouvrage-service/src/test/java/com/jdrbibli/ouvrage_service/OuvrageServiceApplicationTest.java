package com.jdrbibli.ouvrage_service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OuvrageServiceApplicationTest {

    @Test
    void application_canBeInstantiated() {
        OuvrageServiceApplication app = new OuvrageServiceApplication();
        assertThat(app).isNotNull();
    }
}
