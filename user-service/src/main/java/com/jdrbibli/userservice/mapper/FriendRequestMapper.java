package com.jdrbibli.userservice.mapper;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.FriendRequestDTO;
import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.UserProfile;

public class FriendRequestMapper {

    public static FriendRequestDTO toDTO(FriendRequest request) {
        FriendRequestDTO dto = new FriendRequestDTO();
        dto.setId(request.getId());
        dto.setSenderId(request.getSender().getId());
        dto.setSenderPseudo(request.getSender().getPseudo());
        dto.setReceiverId(request.getReceiver().getId());
        dto.setReceiverPseudo(request.getReceiver().getPseudo());
        dto.setStatus(request.getStatus().name());
        return dto;
    }

    public static FriendDTO toDTO(UserProfile user) {
        FriendDTO dto = new FriendDTO();
        dto.setId(user.getId());
        dto.setPseudo(user.getPseudo());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }
}
