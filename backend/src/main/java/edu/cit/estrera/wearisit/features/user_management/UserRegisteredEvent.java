package edu.cit.estrera.wearisit.features.user_management;

import lombok.Getter;

@Getter
public class UserRegisteredEvent {

    private final User user;

    public UserRegisteredEvent(User user) {
        this.user = user;
    }
}