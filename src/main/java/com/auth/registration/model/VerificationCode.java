package com.auth.registration.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "verification_codes")
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Код не может быть пустым")
    private String code;

    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email обязателен")
    private String email;

    @NotNull(message = "Дата истечения должна быть указана")
    @Future(message = "Дата истечения должна быть в будущем")
    private LocalDateTime expiryDate;

    private boolean used;

    public @NotBlank(message = "Код не может быть пустым") String getCode() {
        return code;
    }

    public void setCode(@NotBlank(message = "Код не может быть пустым") String code) {
        this.code = code;
    }

    public @Email(message = "Некорректный формат email") @NotBlank(message = "Email обязателен") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Некорректный формат email") @NotBlank(message = "Email обязателен") String email) {
        this.email = email;
    }

    public @NotNull(message = "Дата истечения должна быть указана") @Future(message = "Дата истечения должна быть в будущем") LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(@NotNull(message = "Дата истечения должна быть указана") @Future(message = "Дата истечения должна быть в будущем") LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}