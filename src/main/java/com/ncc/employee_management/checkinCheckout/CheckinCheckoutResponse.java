package com.ncc.employee_management.checkinCheckout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckinCheckoutResponse {
    private Integer id;

    private Integer userId;

    private String username;

    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;

    private LocalDate dayWorking;

    private boolean isWorking;
    private boolean isCheckinLate;
    private boolean isCheckoutEarly;
}
