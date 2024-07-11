package com.ncc.employee_management.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendSimpleEmail(String toEmail,
                                String subject,
                                String body
    ) {
        System.out.println("Current thread: " + Thread.currentThread().getName());
        log.info("Current thread: " + Thread.currentThread().getName());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hxluong1611@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        mailSender.send(message);
        System.out.println("Mail Send...");
    }


}
