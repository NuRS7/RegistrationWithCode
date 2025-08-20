package com.auth.registration.service;

import com.auth.registration.model.User;
import com.auth.registration.model.VerificationCode;
import com.auth.registration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
    }


    @Override
    public void initiateRegistration(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Емайл уже существует");
        }
        VerificationCode code = emailVerificationService.createVerificationCode(email);
        emailVerificationService.sendVerificationCode(email, code.getCode());

    }

    @Override
    public User completeRegistration(String email, String password, String code) {
        if (!emailVerificationService.validateCode(email, code)) {
            throw new RuntimeException("Неверный или просрочный код");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Override
    public boolean isEmailRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

}
