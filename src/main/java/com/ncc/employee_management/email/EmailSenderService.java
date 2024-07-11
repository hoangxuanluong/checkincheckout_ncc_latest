package com.ncc.employee_management.email;

public interface EmailSenderService {

    void sendSimpleEmail(String toEmail, String subject, String body);
}
