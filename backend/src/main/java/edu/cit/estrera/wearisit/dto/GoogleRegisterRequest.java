package edu.cit.estrera.wearisit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleRegisterRequest {
    @NotBlank(message = "Google ID token is required")
    private String idToken;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;
}