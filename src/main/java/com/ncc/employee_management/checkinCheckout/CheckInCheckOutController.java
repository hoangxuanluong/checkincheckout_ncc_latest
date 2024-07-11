package com.ncc.employee_management.checkinCheckout;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
//@Component
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
    public CheckinCheckoutPage getUserRecords(@PathVariable Integer userId,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                              @RequestParam(required = false) Integer pageNo,
                                              @RequestParam(required = false) Integer pageSize) {
        return checkInCheckOutService.getUserRecords(userId, startDate, endDate, pageNo, pageSize);
    }


    @GetMapping("/records")
    public CheckinCheckoutPage getAllRecordsByDayWorkingBetween(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                @RequestParam(required = false) Integer pageNo,
                                                                @RequestParam(required = false) Integer pageSize) {
        return checkInCheckOutService.getAllRecordsByDayWorkingBetween(startDate, endDate, pageNo, pageSize);
    }

//    @GetMapping("/records")
//    public List<CheckInCheckOut> getAllRecords(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//        return checkInCheckOutService.getAllRecords(startDate, endDate);
//    }


    @GetMapping("/errors/monthly")
    public CheckinCheckoutPage getMonthlyCheckinErrors(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                       @RequestParam(required = false) Integer pageNo,
                                                       @RequestParam(required = false) Integer pageSize) {
        if (startDate == null || endDate == null) {
            LocalDate now = LocalDate.now();
            startDate = now.withDayOfMonth(1);
            endDate = now.withDayOfMonth(now.lengthOfMonth());
        }
        return checkInCheckOutService.getMonthlyCheckinErrors(startDate, endDate, pageNo, pageSize);
    }

    @GetMapping("/errors/monthly/{userId}")
    public CheckinCheckoutPage getMonthlyCheckinErrorsForUser(@PathVariable Integer userId,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                              @RequestParam(required = false) Integer pageNo,
                                                              @RequestParam(required = false) Integer pageSize) {
        if (startDate == null || endDate == null) {
            LocalDate now = LocalDate.now();
            startDate = now.withDayOfMonth(1);
            endDate = now.withDayOfMonth(now.lengthOfMonth());
        }
        return checkInCheckOutService.getMonthlyCheckinErrorsForUser(userId, startDate, endDate, pageNo, pageSize);
    }
}
