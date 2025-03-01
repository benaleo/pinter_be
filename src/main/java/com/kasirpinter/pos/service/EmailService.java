package com.kasirpinter.pos.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendOtpMessage(String identity, String email, String name, String otpCode) throws MessagingException;
}
