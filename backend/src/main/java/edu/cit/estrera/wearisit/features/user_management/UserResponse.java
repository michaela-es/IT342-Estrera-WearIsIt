package edu.cit.estrera.wearisit.features.user_management;

public class UserResponse {

    private String email;
    private String username;

    public UserResponse() {}

    private UserResponse(Builder builder) {
        this.email = builder.email;
        this.username = builder.username;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String email;
        private String username;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }


        public UserResponse build() {
            return new UserResponse(this);
        }
    }
}