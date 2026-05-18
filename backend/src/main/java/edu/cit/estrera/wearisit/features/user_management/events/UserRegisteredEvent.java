package edu.cit.estrera.wearisit.features.user_management.events;

import edu.cit.estrera.wearisit.features.user_management.User;
import lombok.Getter;

@Getter
public class UserRegisteredEvent {

    private final User user;

    public UserRegisteredEvent(User user) {
        this.user = user;
    }
}