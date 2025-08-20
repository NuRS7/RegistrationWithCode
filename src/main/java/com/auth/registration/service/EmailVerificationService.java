package com.auth.registration.service;


import com.auth.registration.model.VerificationCode;
import com.auth.registration.repository.VerificationRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class EmailVerificationService {
    private final JavaMailSender mailSender;
    private final VerificationRepository verificationRepository;

    public EmailVerificationService(JavaMailSender mailSender, VerificationRepository verificationRepository) {
        this.mailSender = mailSender;
        this.verificationRepository = verificationRepository;
    }

    public String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    public void sendVerificationCode(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Код подтверждения регистрации");

            String html = """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            padding: 20px;
                            margin: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: auto;
                            background: #ffffff;
                            border-radius: 12px;
                            padding: 30px;
                            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
                            text-align: center;
                        }
                        .logo {
                            margin-bottom: 20px;
                        }
                        .title {
                            font-size: 22px;
                            font-weight: bold;
                            color: #333333;
                            margin-bottom: 10px;
                        }
                        .subtitle {
                            font-size: 16px;
                            color: #555555;
                            margin-bottom: 20px;
                        }
                        .code {
                            font-size: 28px;
                            font-weight: bold;
                            color: #2d89ef;
                            letter-spacing: 4px;
                            margin: 20px 0;
                        }
                        .btn {
                            display: inline-block;
                            padding: 12px 25px;
                            background-color: #2d89ef;
                            color: #ffffff !important;
                            text-decoration: none;
                            border-radius: 6px;
                            font-size: 16px;
                            margin-top: 20px;
                        }
                        .footer {
                            font-size: 12px;
                            color: #999999;
                            margin-top: 30px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="logo">
                            <img src="https://cdn-icons-png.flaticon.com/512/295/295128.png" width="70" alt="logo"/>
                        </div>
                        <div class="title">Добро пожаловать в наш сервис!</div>
                        <div class="subtitle">Для завершения регистрации используйте код подтверждения:</div>
                        <div class="code">%s</div>
                        <div class="footer">
                            Если вы не регистрировались, просто игнорируйте это письмо.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(code);


            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка отправки email", e);
        }
    }

    public VerificationCode createVerificationCode(String email) {
        verificationRepository.deleteByEmail(email);

        String code = generateCode();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setEmail(email);
        verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        verificationCode.setUsed(false);
        return verificationRepository.save(verificationCode);
    }

    public boolean validateCode(String email, String code) {
        Optional<VerificationCode> verificationCodeOpt =
                verificationRepository.findByCodeAndEmail(code, email);

        if (verificationCodeOpt.isEmpty()) {
            return false;
        }
        VerificationCode verificationCode = verificationCodeOpt.get();

        if (verificationCode.isUsed()) {
            verificationCode.getExpiryDate().isBefore(LocalDateTime.now());
            return false;
        }
        verificationCode.setUsed(true);
        verificationRepository.save(verificationCode);
        return true;
    }

}
