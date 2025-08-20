package com.auth.registration.service;

import com.auth.registration.model.User;

public interface UserService {
    void initiateRegistration(String email, String password);

    User completeRegistration(String email, String password, String code);

    boolean isEmailRegistered(String email);

}
