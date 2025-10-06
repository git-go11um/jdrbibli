package com.jdrbibli.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Tests unitaires pour {@link GatewayApplication}.
 */
@SpringBootTest
class GatewayApplicationTest {

	/**
	 * Vérifie que le contexte Spring se charge correctement.
	 */
	@Test
	void contextLoads() {
		// Si le contexte ne se charge pas, ce test échoue automatiquement
	}

	/**
	 * Vérifie que la méthode main peut être exécutée sans lever d'exception.
	 */
	@Test
	void mainMethodRunsWithoutException() {
		assertDoesNotThrow(() -> GatewayApplication.main(new String[] {}));
	}
}
