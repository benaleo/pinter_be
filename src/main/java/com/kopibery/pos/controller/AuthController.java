package com.kopibery.pos.controller;

import com.kopibery.pos.entity.Users;
import com.kopibery.pos.model.AuthModel;
import com.kopibery.pos.repository.UserRepository;
import com.kopibery.pos.security.JwtUtil;
import com.kopibery.pos.service.util.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody AuthModel.loginRequest request) {
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

            // Generate JWT token after successful authentication
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            log.error("Error : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

}
