package com.ncc.employee_management.serviceImpl;

import com.ncc.employee_management.entity.CheckInCheckOut;
import com.ncc.employee_management.entity.User;
import com.ncc.employee_management.repository.CheckInCheckOutRepository;
import com.ncc.employee_management.repository.UserRepository;
import com.ncc.employee_management.service.EmailSenderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@EnableAsync
public class DailyCheckInReminderJob {

    private final UserRepository userRepository;
    private final CheckInCheckOutRepository checkInCheckOutRepository;
    private final EmailSenderService mailSender;

    @Async
    @Scheduled(cron = "0 06 15 * * ?")
    @Transactional
    public void sendDailyReminders() {

        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return;
        }

        List<User> users = userRepository.findAll();

        users.forEach(user -> {
            CheckInCheckOut record = checkInCheckOutRepository
                    .findByUserAndDayWorking(user, today)
                    .orElseGet(() -> {
                        CheckInCheckOut newRecord = CheckInCheckOut.builder()
                                .user(user)
                                .dayWorking(today)
                                .isWorking(false)
                                .build();
                        return checkInCheckOutRepository.save(newRecord);
                    });
            sendReminderMailToUser(user, record);
        });
    }

    private void sendReminderMailToUser(User user, CheckInCheckOut record) {
        StringBuilder emailBody = new StringBuilder("Daily Check-In Reminder:\n");

        if (record.getCheckinTime() == null) {
            emailBody.append(" - You did not check-in today.\n");
        } else if (record.isCheckinLate()) {
            emailBody.append(" - You checked in late today.\n");
        }

        if (record.getCheckoutTime() == null) {
            emailBody.append(" - You did not check-out today.\n");
        } else if (record.isCheckoutEarly()) {
            emailBody.append(" - You checked out early today.\n");
        }


        mailSender.sendSimpleEmail(user.getEmail(), "Daily Check-In Reminder", emailBody.toString());
    }


}
