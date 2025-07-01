package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.User;

public interface IUserService {
    User inscrireNewUser(String pseudo, String email, String motDePasse);

    User login(String email, String motDePasse);

    void supprimerUser(Long userId);

    UserResponseDTO toDTO(User user);

}
