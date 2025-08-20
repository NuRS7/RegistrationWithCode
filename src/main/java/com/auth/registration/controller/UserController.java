package com.auth.registration.controller;


import com.auth.registration.dto.RegistrationRequest;
import com.auth.registration.dto.VerificationRequest;
import com.auth.registration.model.User;
import com.auth.registration.service.RateLimitService;
import com.auth.registration.service.RegistrationCacheService;
import com.auth.registration.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/auth")
@Tag(name = "Регистрация пользователя с помощью код подтверждение", description = "SMTP через сервер отправляем на почту code верификации " +
        "пользователь должен ввести его, а так же имеется возможность получить кэширование с помощью Redis")

public class UserController {
    private final UserService userService;

    private final RegistrationCacheService cacheService;
    private final RateLimitService rateLimitService;

    @Autowired
    public UserController(UserService userService, RegistrationCacheService cacheService, RateLimitService rateLimitService) {
        this.userService = userService;
        this.cacheService = cacheService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping("/register/initiate")
    @Operation(
            summary = "Отправка и получение код подтверждение на email пошту",
            description = "Создает пользователя с email и паролем",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешная регистрация",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Успешный ответ",
                                            value = "{ \"message\": \"Регистрация успешна, подтвердите email\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ошибка валидации",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Ошибка",
                                            value = "{ \"error\": \"Email уже используется\" }"

                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> initiateRegistration(@RequestBody RegistrationRequest request) {
        if (!rateLimitService.tryAcquire(request.getEmail())) {
            return ResponseEntity.status(429).body(Map.of("eroor", "Слишком много попыток! Попробуйте через час..."));
        }
        try {
            cacheService.saveTemporaryUser(request.getEmail(), request.getPassword());
            userService.initiateRegistration(request.getEmail(), request.getPassword());
            return ResponseEntity.ok().body(Map.of("message", "Код отправлен на email почту"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register/complete")
    public ResponseEntity<?> completeRegistration(@RequestBody VerificationRequest verificationRequest) {
        try {
            // Проверяем, есть ли данные в кеше
            String cachedPassword = cacheService.getTemporaryUser(verificationRequest.getEmail());
            if (cachedPassword == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Время регистрации истекло. Начните заново."
                ));
            }

            // Проверяем пароль из кеша
            if (!cachedPassword.equals(verificationRequest.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Неверные данные. Начните регистрацию заново."
                ));
            }
            User user = userService.completeRegistration(
                    verificationRequest.getEmail(),
                    verificationRequest.getPassword(),
                    verificationRequest.getCode()
            );
            return ResponseEntity.ok().body(Map.of("message", "Регистрация завершена", "userID", user.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

    }

    @PostMapping("/register/status")
    public ResponseEntity<?> checkRegistrationStatus(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email обязателен"));
        }

        boolean hasTemporaryData = cacheService.hasTemporaryUser(email);
        boolean isRegistered = userService.isEmailRegistered(email);

        return ResponseEntity.ok().body(Map.of(
                "hasTemporaryData", hasTemporaryData,
                "isRegistered", isRegistered,
                "email", email
        ));
    }

}
