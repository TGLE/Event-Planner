package com.tgle.planner.core.email;

import com.tgle.planner.core.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailAuthenticationService {

    private final ApplicationProperties application;
    private final EmailTemplateBuilder emailTemplateBuilder;
    private final EmailService emailService;

    public void sendEmailVerification(String fullName, String email, String tokenValue) {
        sendEmail(EmailTemplate.EMAIL_VERIFY, email, verificationVariables(fullName, tokenValue));
    }

    public void sendPasswordResetVerification(String fullName, String email, String tokenValue) {
        sendEmail(EmailTemplate.PASSWORD_RESET, email, verificationVariables(fullName, tokenValue));
    }

    public void sendEmailChangeVerification(String fullName, String email, String tokenValue) {
        sendEmail(EmailTemplate.EMAIL_CHANGE, email, verificationVariables(fullName, tokenValue));
    }

    public void sendPasswordResetConfirmation(String fullName, String email) {
        sendEmail(EmailTemplate.PASSWORD_RESET_CONFIRMATION, email, Map.of("full_name", fullName));
    }

    public void sendPasswordChangeConfirmation(String fullName, String email) {
        sendEmail(EmailTemplate.PASSWORD_CHANGE_CONFIRMATION, email, Map.of("full_name", fullName));
    }

    public void sendEmailChangeConfirmation(String fullName, String oldEmail, String newEmail) {
        sendEmail(EmailTemplate.EMAIL_CHANGE_CONFIRMATION, oldEmail, Map.of(
                "full_name", fullName,
                "old_email", oldEmail,
                "new_email", maskEmail(newEmail)
        ));
    }

    private Map<String, Object> verificationVariables(String fullName, String tokenValue) {
        return Map.of(
                "full_name", fullName,
                "token_value", tokenValue
        );
    }

    private void sendEmail(EmailTemplate template, String email, Map<String, Object> variables) {
        RenderedEmail renderedEmail = emailTemplateBuilder.buildEmail(template, variables);
        emailService.sendEmail(application.name(), email, renderedEmail.subject(), renderedEmail.body());
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        return email.replaceAll("(^.)[^@]*(@.*)", "$1***$2");
    }
}
