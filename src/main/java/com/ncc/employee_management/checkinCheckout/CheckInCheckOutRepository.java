package com.ncc.employee_management.checkinCheckout;

import com.ncc.employee_management.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CheckInCheckOutRepository extends JpaRepository<CheckInCheckOut, Integer> {
    Optional<CheckInCheckOut> findByUserAndDayWorking(User user, LocalDate dayWorking);

    Page<CheckInCheckOut> findByUserAndDayWorkingBetween(User user, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<CheckInCheckOut> findByDayWorkingBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

}