package edu.cit.estrera.wearisit.features.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckEmailResponse {
    private String email;
    private boolean whitelisted;
}

