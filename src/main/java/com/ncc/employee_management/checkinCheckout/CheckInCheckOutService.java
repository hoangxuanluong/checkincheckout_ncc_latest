package com.ncc.employee_management.checkinCheckout;

import java.time.LocalDate;

public interface CheckInCheckOutService {
    void checkin(String checkinCode);

    void checkout(String checkinCode);

//    List<CheckinCheckoutResponse> getUserRecords(Integer userId, LocalDate startDate, LocalDate endDate);

    CheckinCheckoutPage getUserRecords(Integer userId, LocalDate startDate, LocalDate endDate, Integer pageNo, Integer pageSize);

    CheckinCheckoutPage getAllRecordsByDayWorkingBetween(LocalDate startDate, LocalDate endDate, Integer page, Integer size);

//    CheckinCheckoutPage getMonthlyCheckinErrors(LocalDate startDate, LocalDate endDate);
//
//    CheckinCheckoutPage getMonthlyCheckinErrorsForUser(Integer userId, LocalDate startDate, LocalDate endDate);

    CheckinCheckoutPage getMonthlyCheckinErrors(LocalDate startDate, LocalDate endDate, Integer pageNo, Integer pageSize);

    CheckinCheckoutPage getMonthlyCheckinErrorsForUser(Integer userId, LocalDate startDate, LocalDate endDate, Integer pageNo, Integer pageSize);
}
