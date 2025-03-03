package com.kasirpinter.pos.service;

import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.model.AuthModel;
import com.kasirpinter.pos.model.AuthModel.registerRequest;

import jakarta.mail.MessagingException;

public interface AuthService {

    void forgotPassword(String email) throws MessagingException;

    boolean validateOtp(String email, String otp);

    void generateAndSendOtp(String identity, Users user) throws MessagingException;

    void setNewPassword(String email, String identifier, AuthModel.resetPasswordRequest dto);

    Object login(String email, String password);

    void registerUser(registerRequest request);
}
