package com.kopibery.pos.service.impl;

import com.kopibery.pos.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${mail.from}")
    private String fromEmail;

    @Override
    public void sendOtpMessage(String identity, String to, String name, String otp) throws MessagingException {
        String headerLogo = "/header";
        String footerLogo = "/footer";
        String  icon = "/icon";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("otp", otp);
        context.setVariable("otp1", otp.charAt(0));
        context.setVariable("otp2", otp.charAt(1));
        context.setVariable("otp3", otp.charAt(2));
        context.setVariable("otp4", otp.charAt(3));
        context.setVariable("otp5", otp.charAt(4));
        context.setVariable("otp6", otp.charAt(5));
        context.setVariable("headerLogo", headerLogo);
        context.setVariable("footerLogo", footerLogo);
        context.setVariable("icon", icon);

        context.setVariable("title1", "Here's your one-time");
        context.setVariable("title2", "password (OTP)");
        context.setVariable("body", "Thanks for registering and being a part of BCA Young Community. To complete your registration, enter this verification code in your app.");

        String template = "emails/otp";
        String subject = "Registration OTP";

        if (Objects.equals(identity, "resend")) {
            subject = "Resend OTP";
            template = "emails/otp";
            context.setVariable("title1", "Here's your one-time");
            context.setVariable("title2", "resend password (OTP)");
            context.setVariable("body", "We received a request to resend otp. Here's your one-time resend password (OTP).");
        } else if (Objects.equals(identity, "reset")) {
            subject = "Forgot Password OTP";
            template = "emails/otp";
            context.setVariable("title1", "Reset your password");
            context.setVariable("title2", null);
            context.setVariable("body", "We received a request to reset your password. Here's your one-time password (OTP).");
        }
        String html = templateEngine.process(template, context);

        helper.setTo(to);
        helper.setText(html, true);
        helper.setSubject(subject);
        helper.setFrom(fromEmail);

        javaMailSender.send(message);
    }
}
