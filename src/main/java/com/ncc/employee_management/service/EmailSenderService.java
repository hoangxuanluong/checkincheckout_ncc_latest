package com.ncc.employee_management.service;

public interface EmailSenderService {

    void sendSimpleEmail(String toEmail, String subject, String body);
}
