package com.jdrbibli.userservice.mapper;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.entity.UserProfile;

/**
 * Classe utilitaire pour mapper un {@link UserProfile} vers un {@link FriendDTO}.
 */
public class FriendMapper {

    /**
     * Convertit un objet UserProfile en FriendDTO.
     *
     * @param user l'utilisateur à convertir
     * @return un FriendDTO contenant les informations de l'utilisateur, ou null si l'utilisateur est null
     */
    public static FriendDTO toDTO(UserProfile user) {
        if (user == null) return null;

        FriendDTO dto = new FriendDTO();
        dto.setId(user.getId());
        dto.setPseudo(user.getPseudo());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }
}
