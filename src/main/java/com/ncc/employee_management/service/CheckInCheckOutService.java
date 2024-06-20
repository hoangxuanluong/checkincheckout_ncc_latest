package com.ncc.employee_management.service;

import com.ncc.employee_management.entity.CheckInCheckOut;

import java.time.LocalDate;
import java.util.List;

public interface CheckInCheckOutService {
    void checkin(String checkinCode);

    void checkout(String checkinCode);

    List<CheckInCheckOut> getUserRecords(Integer userId, LocalDate startDate, LocalDate endDate);

    List<CheckInCheckOut> getAllRecords(LocalDate startDate, LocalDate endDate);

    List<CheckInCheckOut> getMonthlyCheckinErrors(LocalDate startDate, LocalDate endDate);

    List<CheckInCheckOut> getMonthlyCheckinErrorsForUser(Integer userId, LocalDate startDate, LocalDate endDate);
}
