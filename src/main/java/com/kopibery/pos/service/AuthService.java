package com.kopibery.pos.service;

import com.kopibery.pos.entity.Users;
import com.kopibery.pos.model.AuthModel;
import jakarta.mail.MessagingException;

public interface AuthService {

    void forgotPassword(String email) throws MessagingException;

    boolean validateOtp(String email, String otp);

    void generateAndSendOtp(String identity, Users user) throws MessagingException;

    void setNewPassword(String email, String identifier, AuthModel.resetPasswordRequest dto);
}
