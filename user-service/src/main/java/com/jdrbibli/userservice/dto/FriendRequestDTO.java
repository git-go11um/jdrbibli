package com.jdrbibli.userservice.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object représentant une demande d'amitié entre deux utilisateurs.
 * 
 * Contient les informations sur l'expéditeur, le destinataire, le statut de la demande,
 * ainsi que les dates de création et de réponse.
 */
public class FriendRequestDTO {

    /** Identifiant unique de la demande d'amitié */
    private Long id;

    /** Identifiant de l'utilisateur qui envoie la demande */
    private Long senderId;

    /** Pseudo de l'utilisateur qui envoie la demande */
    private String senderPseudo;

    /** Identifiant de l'utilisateur qui reçoit la demande */
    private Long receiverId;

    /** Pseudo de l'utilisateur qui reçoit la demande */
    private String receiverPseudo;

    /** Statut de la demande (ex. PENDING, ACCEPTED, REJECTED) */
    private String status;

    /** Date et heure de création de la demande */
    private LocalDateTime createdAt;

    /** Date et heure de réponse à la demande (acceptation ou rejet) */
    private LocalDateTime respondedAt;

    /** Constructeur par défaut */
    public FriendRequestDTO() {
    }

    /** Retourne l'identifiant unique de la demande */
    public Long getId() {
        return id;
    }

    /** Définit l'identifiant unique de la demande */
    public void setId(Long id) {
        this.id = id;
    }

    /** Retourne l'identifiant de l'expéditeur */
    public Long getSenderId() {
        return senderId;
    }

    /** Définit l'identifiant de l'expéditeur */
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    /** Retourne le pseudo de l'expéditeur */
    public String getSenderPseudo() {
        return senderPseudo;
    }

    /** Définit le pseudo de l'expéditeur */
    public void setSenderPseudo(String senderPseudo) {
        this.senderPseudo = senderPseudo;
    }

    /** Retourne l'identifiant du destinataire */
    public Long getReceiverId() {
        return receiverId;
    }

    /** Définit l'identifiant du destinataire */
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    /** Retourne le pseudo du destinataire */
    public String getReceiverPseudo() {
        return receiverPseudo;
    }

    /** Définit le pseudo du destinataire */
    public void setReceiverPseudo(String receiverPseudo) {
        this.receiverPseudo = receiverPseudo;
    }

    /** Retourne le statut de la demande */
    public String getStatus() {
        return status;
    }

    /** Définit le statut de la demande */
    public void setStatus(String status) {
        this.status = status;
    }

    /** Retourne la date de création de la demande */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** Définit la date de création de la demande */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /** Retourne la date de réponse à la demande */
    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    /** Définit la date de réponse à la demande */
    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }
}
