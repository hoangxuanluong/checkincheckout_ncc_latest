package com.ncc.employee_management.repository;

import com.ncc.employee_management.entity.CheckInCheckOut;
import com.ncc.employee_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CheckInCheckOutRepository extends JpaRepository<CheckInCheckOut, Integer> {
    Optional<CheckInCheckOut> findByUserAndDayWorking(User user, LocalDate dayWorking);

    List<CheckInCheckOut> findByUserAndDayWorkingBetween(User user, LocalDate startDate, LocalDate endDate);

    List<CheckInCheckOut> findByDayWorkingBetween(LocalDate startDate, LocalDate endDate);
}