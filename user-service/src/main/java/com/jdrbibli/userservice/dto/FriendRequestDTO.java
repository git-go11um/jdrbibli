package com.jdrbibli.userservice.dto;

import java.time.LocalDateTime;

public class FriendRequestDTO {
    private Long id;
    private Long senderId;
    private String senderPseudo;
    private Long receiverId;
    private String receiverPseudo;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    public FriendRequestDTO() {
    }

    // Getters / Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderPseudo() {
        return senderPseudo;
    }

    public void setSenderPseudo(String senderPseudo) {
        this.senderPseudo = senderPseudo;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverPseudo() {
        return receiverPseudo;
    }

    public void setReceiverPseudo(String receiverPseudo) {
        this.receiverPseudo = receiverPseudo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }
}
