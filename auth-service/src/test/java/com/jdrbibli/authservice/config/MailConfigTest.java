/* package com.jdrbibli.authservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class MailConfigTest {

    @Test
    void testJavaMailSenderConfiguration() {
        MailConfig config = new MailConfig();

        JavaMailSender sender = config.javaMailSender();
        assertNotNull(sender, "JavaMailSender ne doit pas être null");
        assertTrue(sender instanceof JavaMailSenderImpl, "Doit être une instance de JavaMailSenderImpl");

        JavaMailSenderImpl impl = (JavaMailSenderImpl) sender;

        // Vérification des paramètres principaux
        assertEquals("localhost", impl.getHost());
        assertEquals(25, impl.getPort());

        // Vérification des propriétés
        Properties props = impl.getJavaMailProperties();
        assertEquals("smtp", props.getProperty("mail.transport.protocol"));
        assertEquals("false", props.getProperty("mail.smtp.auth"));
        assertEquals("false", props.getProperty("mail.smtp.starttls.enable"));
        assertEquals("true", props.getProperty("mail.debug"));
    }

    @Test
    void testJavaMailSenderMultipleCalls() {
        MailConfig config = new MailConfig();

        JavaMailSender firstCall = config.javaMailSender();
        JavaMailSender secondCall = config.javaMailSender();

        assertNotNull(firstCall);
        assertNotNull(secondCall);

        // Pas forcément la même instance, mais doit être bien configurée à chaque fois
        assertEquals(((JavaMailSenderImpl) firstCall).getHost(),
                ((JavaMailSenderImpl) secondCall).getHost());
    }
}
 */