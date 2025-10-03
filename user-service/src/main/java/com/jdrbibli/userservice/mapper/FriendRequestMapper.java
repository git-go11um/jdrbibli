package com.jdrbibli.userservice.mapper;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.FriendRequestDTO;
import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.UserProfile;

/**
 * Classe utilitaire pour mapper des entités liées aux amis et aux demandes d'amis
 * vers leurs DTO correspondants.
 */
public class FriendRequestMapper {

    /**
     * Convertit un objet {@link FriendRequest} en {@link FriendRequestDTO}.
     *
     * @param request la demande d'ami à convertir
     * @return un {@link FriendRequestDTO} contenant les informations de la demande,
     *         ou null si la demande est null
     */
    public static FriendRequestDTO toDTO(FriendRequest request) {
        if (request == null)
            return null;

        FriendRequestDTO dto = new FriendRequestDTO();
        dto.setId(request.getId());
        dto.setStatus(request.getStatus() != null ? request.getStatus().name() : null);

        try {
            dto.setCreatedAt(request.getCreatedAt());
        } catch (Exception e) {
            dto.setCreatedAt(null);
        }
        try {
            dto.setRespondedAt(request.getRespondedAt());
        } catch (Exception e) {
            dto.setRespondedAt(null);
        }

        // Sender
        try {
            UserProfile sender = request.getSender();
            if (sender != null) {
                dto.setSenderId(sender.getId());
                try {
                    dto.setSenderPseudo(sender.getPseudo());
                } catch (Exception e) {
                    dto.setSenderPseudo(null);
                }
            }
        } catch (Exception ignored) {
        }

        // Receiver
        try {
            UserProfile receiver = request.getReceiver();
            if (receiver != null) {
                dto.setReceiverId(receiver.getId());
                try {
                    dto.setReceiverPseudo(receiver.getPseudo());
                } catch (Exception e) {
                    dto.setReceiverPseudo(null);
                }
            }
        } catch (Exception ignored) {
        }

        return dto;
    }

    /**
     * Convertit un objet {@link UserProfile} en {@link FriendDTO}.
     *
     * @param user l'utilisateur à convertir
     * @return un {@link FriendDTO} contenant les informations de l'utilisateur,
     *         ou null si l'utilisateur est null
     */
    public static FriendDTO toDTO(UserProfile user) {
        if (user == null)
            return null;
        FriendDTO dto = new FriendDTO();
        dto.setId(user.getId());
        dto.setPseudo(user.getPseudo());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }
}
