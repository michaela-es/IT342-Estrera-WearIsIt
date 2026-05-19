package edu.cit.estrera.wearisit.features.email;

import edu.cit.estrera.wearisit.features.user_management.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handle(UserRegisteredEvent event) {

        log.info("UserRegisteredEvent received: {}", event.getUser().getEmail());

        emailService.sendVerificationEmail(
                event.getUser().getEmail(),
                event.getUser().getUsername()
        );
    }
}