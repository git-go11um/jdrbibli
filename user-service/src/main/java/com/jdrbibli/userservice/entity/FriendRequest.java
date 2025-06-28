package com.jdrbibli.userservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // L'utilisateur qui envoie la demande
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserProfile sender;

    // L'utilisateur qui reçoit la demande
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private UserProfile receiver;

    @Enumerated(EnumType.STRING)
    private Status status; // PENDING, ACCEPTED, REJECTED

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    // Getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserProfile getSender() { return sender; }
    public void setSender(UserProfile sender) { this.sender = sender; }

    public UserProfile getReceiver() { return receiver; }
    public void setReceiver(UserProfile receiver) { this.receiver = receiver; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
