package com.kasirpinter.pos.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.kasirpinter.pos.converter.LogGeneralConverter;
import com.kasirpinter.pos.entity.Company;
import com.kasirpinter.pos.entity.Otp;
import com.kasirpinter.pos.entity.Roles;
import com.kasirpinter.pos.exception.BadRequestException;
import com.kasirpinter.pos.model.AuthModel;
import com.kasirpinter.pos.model.AuthModel.registerRequest;
import com.kasirpinter.pos.model.LogGeneralRequest;
import com.kasirpinter.pos.repository.CompanyRepository;
import com.kasirpinter.pos.repository.OtpRepository;
import com.kasirpinter.pos.repository.RoleRepository;
import com.kasirpinter.pos.service.EmailService;
import com.kasirpinter.pos.service.util.CustomUserDetailsService;
import com.kasirpinter.pos.util.OtpUtil;
import com.kasirpinter.pos.util.RandomStringGenerator;

import jakarta.mail.MessagingException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.security.JwtUtil;
import com.kasirpinter.pos.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogGeneralConverter logGeneralConverter;

    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void forgotPassword(String email) throws MessagingException {
        Optional<Users> optionalUser = userRepository.findByEmailIgnoreCase(email);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            // Invalidate previous OTPs
            otpRepository.invalidateOtpsForUser(user);

            generateAndSendOtp("reset", user);
        } else {
            throw new IllegalArgumentException("User not found");
        }

    }

    @Override
    @Transactional
    public boolean validateOtp(String email, String otpCode) {
        Optional<Users> userOptional = userRepository.findByEmailIgnoreCase(email);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            Optional<Otp> otpOptional = otpRepository.findByUserAndOtpAndValidIsTrue(user, otpCode);
            if (otpOptional.isPresent()) {
                Otp otp = otpOptional.get();
                if (otp.getExpiryDate().isAfter(LocalDateTime.now())) {
                    otp.setValid(false); // Mark OTP as used
                    otpRepository.save(otp);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void generateAndSendOtp(String identity, Users user) throws MessagingException {

        String otpCode = OtpUtil.generateOtp();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15); // OTP valid for 15 minutes

        Otp otp = new Otp(null, user, otpCode, expiryDate, true);
        otpRepository.save(otp);

        // Send OTP to user's email
        emailService.sendOtpMessage(identity, user.getEmail(), user.getName(), otpCode);

    }

    @Override
    @Transactional
    public void setNewPassword(String email, String identifier, AuthModel.resetPasswordRequest dto) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!dto.isSetPasswordMatch()) {
            throw new BadRequestException("Password does not match");
        }

        if (!"reset".equals(identifier) && !"profile".equals(identifier)) {
            throw new BadRequestException("Invalid identifier");
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // save
        Users savedUser = userRepository.save(user);

        // send to log
        LogGeneralRequest logRequest = new LogGeneralRequest(
                identifier,
                "reset".equals(identifier) ? "Reset Password" : "Change Password",
                "Change Password",
                "Successfully change the password",
                savedUser.getEmail());
        logGeneralConverter.sendLogHistory(logRequest);

    }

    @Override
    @Transactional
    public Object login(String email, String password) {
        Users user = userRepository.findByEmail(email).orElse(null);
        log.info("User email in: {}", (user != null ? user.getEmail() : "unknown"));

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        log.info("user details username: {}", userDetails.getUsername());
        final String token = jwtUtil.generateToken(userDetails.getUsername());
        log.info("token: {}", token);
        Map<String, String> newMap = new HashMap<>();
        newMap.put("token", token);
        return newMap;
    }

    @Override
    public void registerUser(registerRequest request) {
        Users newUser = new Users();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setIsActive(true);
        newUser.setPhone(request.getPhone());

        Company company = new Company();
        AuthModel.RegisterCompanyRequest registerCompanyRequest = request.getCompany();
        company.setName(registerCompanyRequest.getName());
        company.setAddress(registerCompanyRequest.getAddress());
        company.setCity(registerCompanyRequest.getCity());
        company.setPhone(registerCompanyRequest.getPhone());
        company.setCode(RandomStringGenerator.generateRandomAlphabetString(6));
        Company savedCompany = companyRepository.save(company);
        
        Company childCompany = new Company();
        childCompany.setName(registerCompanyRequest.getName());
        childCompany.setAddress(registerCompanyRequest.getAddress());
        childCompany.setCity(registerCompanyRequest.getCity());
        childCompany.setPhone(registerCompanyRequest.getPhone());
        childCompany.setCode(RandomStringGenerator.generateRandomAlphabetString(6));
        childCompany.setParent(savedCompany);
        companyRepository.save(childCompany);

        newUser.setCompany(childCompany);

        Roles role = roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("Role not found"));
        newUser.setRole(role);

        // Save the new user
        userRepository.save(newUser);

    }

}
