package com.kopibery.pos.controller;

import com.kopibery.pos.entity.Users;
import com.kopibery.pos.exception.BadRequestException;
import com.kopibery.pos.model.AuthModel;
import com.kopibery.pos.repository.UserRepository;
import com.kopibery.pos.response.ApiResponse;
import com.kopibery.pos.security.JWTHeaderTokenExtractor;
import com.kopibery.pos.security.JWTTokenFactory;
import com.kopibery.pos.security.JwtUtil;
import com.kopibery.pos.service.AuthService;
import com.kopibery.pos.service.UserService;
import com.kopibery.pos.service.util.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth API")
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JWTTokenFactory jwtTokenFactory;
    private final JWTHeaderTokenExtractor jwtHeaderTokenExtractor;

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthModel.loginRequest request) {
        try {
            Users user = userRepository.findByEmail(request.getEmail()).orElse(null);
            log.info("User email in: {}", (user != null ? user.getEmail() : "unknown"));

            if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Invalid email or password");
            }

            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            log.info("user details username: {}", userDetails.getUsername());
            final String token = jwtUtil.generateToken(userDetails.getUsername());
            log.info("token: {}", token);
            Map<String, String> newMap = new HashMap<>();
            newMap.put("token", token);

            // Generate JWT token after successful authentication
            return ResponseEntity.ok().body(new ApiResponse(true, "Auth login successfully", newMap));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Auth failed", null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthModel.registerRequest request) {
        try {
            // Check if the email already exists
            Users existingUser = userRepository.findByEmail(request.getEmail()).orElse(null);
            if (existingUser != null) {
                throw new BadRequestException("Email already exists");
            }

            Users newUser = new Users();
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setIsActive(true);

            // Save the new user
            userRepository.save(newUser);

            // Return success response
            return ResponseEntity.ok().body(new ApiResponse(true, "User registered successfully", null));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while registering the user", null));
        }
    }

    // forgot password send otp by email
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        log.info("POST " + "/api/auth/forgot-password endpoint hit");
        try {
            // 
            authService.forgotPassword(email);
            return ResponseEntity.ok().body(new ApiResponse(true, "Forgot password successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while sending the OTP", null));
        }
    }

    // validate otp
    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestParam String email, @RequestParam String otp) {
        log.info("POST " + "/api/auth/validate-otp endpoint hit");
        try {
            boolean isValid = authService.validateOtp(email, otp);
            if (isValid) {
                final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                final String token = jwtTokenFactory.createAccessJWTToken(userDetails.getUsername(), null).getToken();
                return ResponseEntity.ok().body(new ApiResponse(true, "OTP validated successfully", token));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, "Invalid OTP", null));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while validating the OTP", null));
        }
    }

    // reset password
    @SecurityRequirement(name = "Authorization")
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam(value = "id", required = false) String identifier,
            @RequestBody AuthModel.resetPasswordRequest dto) {
        log.info("PUT " + "/api/auth/reset-password endpoint hit");
        // Extract the email from the token authorizationHeader
        String email = getEmailFromAuthHeader(authorizationHeader);
        Users user = userService.findByEmail(email);

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("password", dto.getPassword());

        try {
            // If user not found
            if (user == null) {
                log.error("User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found", null));
            }

            // Set the new password
            authService.setNewPassword(email, identifier, dto);
            log.info("Password set successfully for user: {}", email);

            return ResponseEntity.ok(new ApiResponse(true, "Password set successfully", null));

        } catch (BadRequestException e) {
            log.error("BadRequestException: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // get user from header auth
    private String getEmailFromAuthHeader(String authorizationHeader) {
        String tokenz = jwtHeaderTokenExtractor.extract(authorizationHeader);
        log.info("Extracted token: {}", tokenz);

        // Extract the email from the token
        String email = jwtHeaderTokenExtractor.getEmail(tokenz);
        log.info("Extracted email from token: {}", email);

        // Find the user by email
        return email;
    }


}
