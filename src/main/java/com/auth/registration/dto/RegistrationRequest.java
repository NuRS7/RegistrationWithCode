package com.auth.registration.dto;


import io.swagger.v3.oas.annotations.media.Schema;

public class RegistrationRequest {
    @Schema(description = "Email пользователя",
            example = "user@gmail.com")
    private String email;
    @Schema(
            description = "Пароль (минимум 8 символов, 1 заглавная, 1 цифра, 1 спецсимвол)",
            example = "Password123!"
    )
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
