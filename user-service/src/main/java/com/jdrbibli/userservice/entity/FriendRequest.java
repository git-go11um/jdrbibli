package com.jdrbibli.userservice.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * Entité représentant une demande d'amitié entre deux utilisateurs.
 */
@Entity
@Table(name = "friend_requests", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "sender_id", "receiver_id" })
})
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Utilisateur qui envoie la demande */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private UserProfile sender;

    /** Utilisateur qui reçoit la demande */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private UserProfile receiver;

    /** Statut de la demande */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    /** Date de création de la demande */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Date à laquelle la demande a été acceptée ou rejetée */
    @Column
    private LocalDateTime respondedAt;

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

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.status == Status.ACCEPTED || this.status == Status.REJECTED) {
            this.respondedAt = LocalDateTime.now();
        }
    }

    // --- Getters / Setters ---
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "id=" + id +
                ", sender=" + (sender != null ? sender.getId() : null) +
                ", receiver=" + (receiver != null ? receiver.getId() : null) +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", respondedAt=" + respondedAt +
                '}';
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
