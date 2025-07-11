package com.jdrbibli.authservice.service;

import org.springframework.mail.javamail.JavaMailSender;

import com.jdrbibli.authservice.dto.ChangePasswordRequest;
import com.jdrbibli.authservice.dto.UserResponseDTO;
import com.jdrbibli.authservice.entity.User;

import jakarta.mail.MessagingException;

public interface IUserService {
    User inscrireNewUser(String pseudo, String email, String motDePasse);

    User login(String email, String motDePasse);

    UserResponseDTO toDTO(User user);

    User getUserByPseudo(String pseudo);

    void changePassword(String userEmail, ChangePasswordRequest request);

    boolean validateResetCode(String pseudo, String code);

    void requestPasswordReset(String pseudo) throws MessagingException;

    void resetPassword(String pseudo, String resetCode, String newPassword);

    void deleteUserById(Long userId);

    void updateUserProfile(Long userId, String newPseudo, String newEmail);

}
