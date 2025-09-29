package com.jdrbibli.userservice.mapper;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.FriendRequestDTO;
import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.UserProfile;

public class FriendRequestMapper {

    public static FriendRequestDTO toDTO(FriendRequest request) {
        if (request == null)
            return null;

        FriendRequestDTO dto = new FriendRequestDTO();
        dto.setId(request.getId());
        dto.setStatus(request.getStatus() != null ? request.getStatus().name() : null);

        // Dates : on essaye de les prendre, mais on protège contre d'éventuelles
        // exceptions
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
            // protège contre LazyInitializationException si la session est fermée
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
            // protège contre LazyInitializationException si la session est fermée
        }

        return dto;
    }

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
