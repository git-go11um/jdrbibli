package com.jdrbibli.userservice.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FriendRequestTest {

    @Test
    void testGettersAndSetters() {
        FriendRequest fr = new FriendRequest();

        Long id = 123L;
        UserProfile sender = new UserProfile();
        sender.setId(1L);
        UserProfile receiver = new UserProfile();
        receiver.setId(2L);
        FriendRequest.Status status = FriendRequest.Status.PENDING;

        fr.setId(id);
        fr.setSender(sender);
        fr.setReceiver(receiver);
        fr.setStatus(status);

        assertEquals(id, fr.getId());
        assertEquals(sender, fr.getSender());
        assertEquals(receiver, fr.getReceiver());
        assertEquals(status, fr.getStatus());
    }

    @Test
    void testEnumValues() {
        assertEquals("PENDING", FriendRequest.Status.PENDING.name());
        assertEquals("ACCEPTED", FriendRequest.Status.ACCEPTED.name());
        assertEquals("REJECTED", FriendRequest.Status.REJECTED.name());
    }
}
