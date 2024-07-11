package com.ncc.employee_management.checkinCheckout;

import com.ncc.employee_management.user.User;
import com.ncc.employee_management.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckinCheckoutMapper {

    private final UserRepository userRepository;

    public CheckinCheckoutResponse toResponse(CheckInCheckOut checkInCheckOut) {
        User user = userRepository.findById(checkInCheckOut.getUser().getId()).orElseThrow(EntityNotFoundException::new);

        return CheckinCheckoutResponse.builder()
                .id(checkInCheckOut.getId())
                .userId(user.getId())
                .username(user.getUsername())
                .checkinTime(checkInCheckOut.getCheckinTime())
                .checkoutTime(checkInCheckOut.getCheckoutTime())
                .dayWorking(checkInCheckOut.getDayWorking())
                .isWorking(checkInCheckOut.isWorking())
                .isCheckoutEarly(checkInCheckOut.isCheckoutEarly())
                .isCheckinLate(checkInCheckOut.isCheckinLate())
                .build();
    }
}
