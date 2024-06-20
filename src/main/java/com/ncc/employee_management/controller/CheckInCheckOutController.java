package com.ncc.employee_management.controller;

import com.ncc.employee_management.entity.CheckInCheckOut;
import com.ncc.employee_management.service.CheckInCheckOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/checkincheckouts")
@RequiredArgsConstructor
public class CheckInCheckOutController {


    private final CheckInCheckOutService checkInCheckOutService;

    @PostMapping("/checkin")
    public void checkin(@RequestParam String checkinCode) {
        checkInCheckOutService.checkin(checkinCode);
    }

    @PostMapping("/checkout")
    public void checkout(@RequestParam String checkinCode) {
        checkInCheckOutService.checkout(checkinCode);
    }

    @GetMapping("/records/{userId}")
    public List<CheckInCheckOut> getUserRecords(@PathVariable Integer userId,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return checkInCheckOutService.getUserRecords(userId, startDate, endDate);
    }


    @GetMapping("/records")
    public List<CheckInCheckOut> getAllRecords(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return checkInCheckOutService.getAllRecords(startDate, endDate);
    }


    @GetMapping("/errors/monthly")
    public List<CheckInCheckOut> getMonthlyCheckinErrors(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null || endDate == null) {
            LocalDate now = LocalDate.now();
            startDate = now.withDayOfMonth(1);
            endDate = now.withDayOfMonth(now.lengthOfMonth());
        }
        return checkInCheckOutService.getMonthlyCheckinErrors(startDate, endDate);
    }

    @GetMapping("/errors/monthly/{userId}")
    public List<CheckInCheckOut> getMonthlyCheckinErrorsForUser(@PathVariable Integer userId,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (startDate == null || endDate == null) {
            LocalDate now = LocalDate.now();
            startDate = now.withDayOfMonth(1);
            endDate = now.withDayOfMonth(now.lengthOfMonth());
        }
        return checkInCheckOutService.getMonthlyCheckinErrorsForUser(userId, startDate, endDate);
    }
}
