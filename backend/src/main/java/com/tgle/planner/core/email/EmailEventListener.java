package com.tgle.planner.core.email;

import com.tgle.planner.authentication.application.event.VerifyEmailRequestedEvent;
import com.tgle.planner.profile.application.event.PasswordChangeConfirmedEvent;
import com.tgle.planner.authentication.application.event.PasswordResetRequestedEvent;
import com.tgle.planner.authentication.application.event.PasswordResetConfirmedEvent;
import com.tgle.planner.profile.application.event.EmailChangeRequestedEvent;
import com.tgle.planner.profile.application.event.EmailChangeConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailAuthenticationService emailAuthenticationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleVerifyEmailRequested(VerifyEmailRequestedEvent event) {
        emailAuthenticationService.sendEmailVerification(event.fullName(), event.email(), event.token());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePasswordResetRequested(PasswordResetRequestedEvent event) {
        emailAuthenticationService.sendPasswordResetVerification(event.fullName(), event.email(), event.token());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePasswordChangeConfirmed(PasswordChangeConfirmedEvent event) {
        emailAuthenticationService.sendPasswordChangeConfirmation(event.fullName(), event.email());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePasswordResetConfirmed(PasswordResetConfirmedEvent event) {
        emailAuthenticationService.sendPasswordResetConfirmation(event.fullName(), event.email());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailChangeRequested(EmailChangeRequestedEvent event) {
        emailAuthenticationService.sendEmailChangeVerification(event.fullName(), event.newEmail(), event.token());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailChangeConfirmed(EmailChangeConfirmedEvent event) {
        emailAuthenticationService.sendEmailChangeConfirmation(event.fullName(), event.currentEmail(), event.newEmail());
    }
}
