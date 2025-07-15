package com.jdrbibli.ouvrage_service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Classe test dédiée à l’exception handler
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    // Dummy controller qui va juste lancer l'exception pour tester le handler
    @org.springframework.web.bind.annotation.RestController
    static class TestController {
        @org.springframework.web.bind.annotation.GetMapping("/notfound")
        public void throwNotFound() {
            throw new ResourceNotFoundException("Ressource inexistante");
        }
    }

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleResourceNotFoundException_returns404_withProperBody() throws Exception {
        mockMvc.perform(get("/notfound"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"))
                .andExpect(jsonPath("$.message").value("Ressource inexistante"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
