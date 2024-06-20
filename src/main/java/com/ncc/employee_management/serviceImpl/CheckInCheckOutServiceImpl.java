package com.ncc.employee_management.serviceImpl;

import com.ncc.employee_management.entity.CheckInCheckOut;
import com.ncc.employee_management.entity.User;
import com.ncc.employee_management.repository.CheckInCheckOutRepository;
import com.ncc.employee_management.repository.UserRepository;
import com.ncc.employee_management.service.CheckInCheckOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckInCheckOutServiceImpl implements CheckInCheckOutService {

    private final CheckInCheckOutRepository checkInCheckOutRepository;

    private final UserRepository userRepository;


    @Override
    public void checkin(String checkinCode) {
        System.out.println(checkinCode);
        User user = userRepository.findByCheckinCode(checkinCode);
        if (user == null) {
            throw new RuntimeException("Invalid check-in code");
        }

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        if (checkInCheckOutRepository.findByUserAndDayWorking(user, today).isPresent()) {
            throw new RuntimeException("Already checked in today");
        }

        CheckInCheckOut checkInCheckOut = CheckInCheckOut.builder()
                .user(user)
                .checkinTime(now)
                .dayWorking(today)
                .isCheckinLate(now.isAfter(LocalDateTime.of(today, LocalTime.of(8, 45))))
                .isWorking(true)
                .build();

        checkInCheckOutRepository.save(checkInCheckOut);
    }

    @Override
    public void checkout(String checkinCode) {
        //Interable
        //Pageble / Pageable sortedByPriceDescNameAsc =
        //----- PageRequest.of(0, 5, Sort.by("price").descending().and(Sort.by("name")));
//        PageRequest.of
        //List<User> findAllByNameLike(String name, Pageable pageable);
//        Sort
        //

        List<User> all = userRepository.findAll(PageRequest.of(0, 10)).toList();
        Pageable page = PageRequest.of(0, 5);

        User user = userRepository.findByCheckinCode(checkinCode);
        if (user == null) {
            throw new RuntimeException("Invalid check-in code");
        }

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        CheckInCheckOut checkInCheckOut = checkInCheckOutRepository.findByUserAndDayWorking(user, today)
                .orElseThrow(() -> new RuntimeException("No check-in record for today"));

        if (checkInCheckOut.getCheckoutTime() != null) {
            throw new RuntimeException("Already checked out today");
        }

        checkInCheckOut.setCheckoutTime(now);
        checkInCheckOut.setCheckoutEarly(now.isBefore(LocalDateTime.of(today, LocalTime.of(17, 30))));

        checkInCheckOutRepository.save(checkInCheckOut);
    }

    @Override
    public List<CheckInCheckOut> getUserRecords(Integer userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            DayOfWeek dayOfWeek = today.getDayOfWeek();
            LocalDate startOfWeek = today.minusDays(dayOfWeek.getValue() - 1);
            LocalDate endOfWeek = today.plusDays(7 - dayOfWeek.getValue());
            startDate = startOfWeek;
            endDate = endOfWeek;
        }

        return checkInCheckOutRepository.findByUserAndDayWorkingBetween(user, startDate, endDate);
    }

    @Override
    public List<CheckInCheckOut> getAllRecords(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            DayOfWeek dayOfWeek = today.getDayOfWeek();
            LocalDate startOfWeek = today.minusDays(dayOfWeek.getValue() - 1);
            LocalDate endOfWeek = today.plusDays(7 - dayOfWeek.getValue());
            startDate = startOfWeek;
            endDate = endOfWeek;
        }
        return checkInCheckOutRepository.findByDayWorkingBetween(startDate, endDate);
    }

    @Override
    public List<CheckInCheckOut> getMonthlyCheckinErrors(LocalDate startDate, LocalDate endDate) {
        return checkInCheckOutRepository.findByDayWorkingBetween(startDate, endDate)
                .stream()
                .filter(record -> record.isCheckinLate() || record.isCheckoutEarly())
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckInCheckOut> getMonthlyCheckinErrorsForUser(Integer userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return checkInCheckOutRepository.findByUserAndDayWorkingBetween(user, startDate, endDate)
                .stream()
                .filter(record -> record.isCheckinLate() || record.isCheckoutEarly())
                .collect(Collectors.toList());
    }

}