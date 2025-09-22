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
    @JoinColumn(name = "sender_id", nullable = false)
    private UserProfile sender;

    // L'utilisateur qui reçoit la demande
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserProfile receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    // Constructeurs
    public FriendRequest() {
    }

    public FriendRequest(UserProfile sender, UserProfile receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = Status.PENDING;
    }

    public FriendRequest(UserProfile sender, UserProfile receiver, Status status) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserProfile getSender() {
        return sender;
    }

    public void setSender(UserProfile sender) {
        this.sender = sender;
    }

    public UserProfile getReceiver() {
        return receiver;
    }

    public void setReceiver(UserProfile receiver) {
        this.receiver = receiver;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isPending() {
        return status == Status.PENDING;
    }

    public boolean isAccepted() {
        return status == Status.ACCEPTED;
    }

    public boolean isRejected() {
        return status == Status.REJECTED;
    }
}
