package com.jdrbibli.authservice.service;

import com.jdrbibli.authservice.entity.User;

public interface IUserService {
    User inscrireNewUser(String pseudo, String email, String motDePasse);
}
