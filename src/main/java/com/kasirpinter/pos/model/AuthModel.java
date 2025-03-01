package com.kasirpinter.pos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthModel {

    @Data
    public static class loginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class registerRequest {
        private String name;
        private String email;
        private String password;
    }

    @Data
    public static class resetPasswordRequest {
        @NotBlank(message = "New Password is required")
        @Size(min = 8, message = "Password must be at least 6 characters")
        private String password;

        @NotBlank(message = "Confirm Password is required")
        @Size(min = 8, message = "Password must be at least 6 characters")
        private String confirmPassword;

        @JsonIgnore
        public boolean isSetPasswordMatch() {
            return password.equals(confirmPassword);
        }
    }
}
