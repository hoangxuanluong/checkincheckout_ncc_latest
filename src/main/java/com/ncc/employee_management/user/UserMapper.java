package com.ncc.employee_management.user;

import com.ncc.employee_management.user.dto.UserCheckinCheckouDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(UserRequest request) {
        return User.builder()
                .id(request.getId())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .build();
    }

    public UserCheckinCheckouDto toResponseWithCheckinCheckout(User user) {
        UserResponse userResponse = toResponse(user);
        return UserCheckinCheckouDto.builder()
                .userResponse(userResponse)
                .checkInCheckOutList(user.getCheckInCheckOutList())
                .build();
    }
}