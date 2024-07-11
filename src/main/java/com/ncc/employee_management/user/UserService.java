package com.ncc.employee_management.user;

import com.ncc.employee_management.user.dto.UserCheckinCheckouDto;

import java.time.LocalDate;
import java.util.List;

public interface UserService {

    UserResponse create(UserRequest request);

    UserResponse findById(Integer id);

    List<UserResponse> findAll();

    void delete(Integer id);

    List<UserResponse> findByFirstname(String firstname);

    List<UserCheckinCheckouDto> getAllEmployeesWithCheckInCheckOut(LocalDate startDate, LocalDate endDate);
}