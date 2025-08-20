package com.auth.registration.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users") // лучше во множественном числе
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$",
            message = "Пароль должен содержать минимум 8 символов, одну заглавную букву, цифру и спецсимвол"
    )
    private String password;

    private boolean enabled;


    public @Email(message = "Некорректный email") @NotBlank(message = "Email обязателен") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Некорректный email") @NotBlank(message = "Email обязателен") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Пароль обязателен") @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$",
            message = "Пароль должен содержать минимум 8 символов, одну заглавную букву, цифру и спецсимвол"
    ) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Пароль обязателен") @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$",
            message = "Пароль должен содержать минимум 8 символов, одну заглавную букву, цифру и спецсимвол"
    ) String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}