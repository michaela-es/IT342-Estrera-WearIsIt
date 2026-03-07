package edu.cit.estrera.wearisit.dto;

public class UserResponse {

    private String email;
    private String username;
    private String role;

    public UserResponse() {}

    private UserResponse(Builder builder) {
        this.email = builder.email;
        this.username = builder.username;
        this.role = builder.role;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String email;
        private String username;
        private String role;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public UserResponse build() {
            return new UserResponse(this);
        }
    }
}