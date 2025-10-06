package com.jdrbibli.userservice.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FriendRequestTest {

    @Test
    void testConstructorsAndStatusHelpers() {
        UserProfile sender = new UserProfile();
        sender.setId(1L);
        UserProfile receiver = new UserProfile();
        receiver.setId(2L);

        // Constructeur par défaut
        FriendRequest fr1 = new FriendRequest();
        fr1.setSender(sender);
        fr1.setReceiver(receiver);
        fr1.setStatus(FriendRequest.Status.PENDING);

        assertEquals(sender, fr1.getSender());
        assertEquals(receiver, fr1.getReceiver());
        assertTrue(fr1.isPending());
        assertFalse(fr1.isAccepted());
        assertFalse(fr1.isRejected());

        // Constructeur avec status
        FriendRequest fr2 = new FriendRequest(sender, receiver, FriendRequest.Status.ACCEPTED);
        assertEquals(FriendRequest.Status.ACCEPTED, fr2.getStatus());
        assertTrue(fr2.isAccepted());
        assertFalse(fr2.isPending());
        assertFalse(fr2.isRejected());
    }

    @Test
    void testPrePersistAndPreUpdate() {
        FriendRequest fr = new FriendRequest();
        fr.setSender(new UserProfile());
        fr.setReceiver(new UserProfile());

        // Simuler @PrePersist
        fr.onCreate();
        assertNotNull(fr.getCreatedAt(), "createdAt doit être défini après onCreate()");

        // Simuler @PreUpdate pour ACCEPTED
        fr.setStatus(FriendRequest.Status.ACCEPTED);
        fr.onUpdate();
        assertNotNull(fr.getRespondedAt(), "respondedAt doit être défini après onUpdate() avec ACCEPTED");

        // Simuler @PreUpdate pour REJECTED
        fr.setStatus(FriendRequest.Status.REJECTED);
        fr.setRespondedAt(null); // reset
        fr.onUpdate();
        assertNotNull(fr.getRespondedAt(), "respondedAt doit être défini après onUpdate() avec REJECTED");

        // Simuler @PreUpdate pour PENDING
        fr.setStatus(FriendRequest.Status.PENDING);
        LocalDateTime old = fr.getRespondedAt();
        fr.onUpdate();
        assertEquals(old, fr.getRespondedAt(), "respondedAt ne doit pas changer si PENDING");
    }
}
