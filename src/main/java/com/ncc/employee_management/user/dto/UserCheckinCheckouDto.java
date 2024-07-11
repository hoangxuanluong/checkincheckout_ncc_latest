package com.ncc.employee_management.user.dto;

import com.ncc.employee_management.checkinCheckout.CheckInCheckOut;
import com.ncc.employee_management.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCheckinCheckouDto {

    private UserResponse userResponse;
    private List<CheckInCheckOut> checkInCheckOutList;
}
