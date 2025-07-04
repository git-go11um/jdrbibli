package com.jdrbibli.userservice.dto;

public class FriendRequestDTO {
    private Long id;
    private Long senderId;
    private String senderPseudo;
    private Long receiverId;
    private String receiverPseudo;
    private String status;

    // Getters et setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderPseudo() { return senderPseudo; }
    public void setSenderPseudo(String senderPseudo) { this.senderPseudo = senderPseudo; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getReceiverPseudo() { return receiverPseudo; }
    public void setReceiverPseudo(String receiverPseudo) { this.receiverPseudo = receiverPseudo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
