package com.jdrbibli.userservice.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FriendRequestDTOTest {

    @Test
    void testGettersAndSetters() {
        FriendRequestDTO request = new FriendRequestDTO();

        Long id = 1L;
        Long senderId = 10L;
        String senderPseudo = "Alice";
        Long receiverId = 20L;
        String receiverPseudo = "Bob";
        String status = "PENDING";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime respondedAt = LocalDateTime.now().plusHours(1);

        // Setters
        request.setId(id);
        request.setSenderId(senderId);
        request.setSenderPseudo(senderPseudo);
        request.setReceiverId(receiverId);
        request.setReceiverPseudo(receiverPseudo);
        request.setStatus(status);
        request.setCreatedAt(createdAt);
        request.setRespondedAt(respondedAt);

        // Getters + assertions
        assertEquals(id, request.getId(), "L'id doit correspondre");
        assertEquals(senderId, request.getSenderId(), "L'id de l'expéditeur doit correspondre");
        assertEquals(senderPseudo, request.getSenderPseudo(), "Le pseudo de l'expéditeur doit correspondre");
        assertEquals(receiverId, request.getReceiverId(), "L'id du destinataire doit correspondre");
        assertEquals(receiverPseudo, request.getReceiverPseudo(), "Le pseudo du destinataire doit correspondre");
        assertEquals(status, request.getStatus(), "Le statut doit correspondre");
        assertEquals(createdAt, request.getCreatedAt(), "La date de création doit correspondre");
        assertEquals(respondedAt, request.getRespondedAt(), "La date de réponse doit correspondre");
    }
}
