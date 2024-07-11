package com.ncc.employee_management.checkinCheckout;

import com.ncc.employee_management.user.User;
import com.ncc.employee_management.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
@Slf4j
public class CheckInCheckOutServiceImpl implements CheckInCheckOutService {

    private final CheckInCheckOutRepository checkInCheckOutRepository;

    private final UserRepository userRepository;

    private final CheckinCheckoutMapper mapper;


    @Override
    public void checkin(String checkinCode) {
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
    public void checkout(String checkinCode) throws RuntimeException {
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
    @Cacheable("checkincheckouts")
    public CheckinCheckoutPage getUserRecords(Integer userId, LocalDate startDate, LocalDate endDate, Integer pageNo, Integer pageSize) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            DayOfWeek dayOfWeek = today.getDayOfWeek();
            LocalDate startOfWeek = today.minusDays(dayOfWeek.getValue() - 1);
            LocalDate endOfWeek = today.plusDays(7 - dayOfWeek.getValue());
            startDate = startOfWeek;
            endDate = endOfWeek;
        }

        if (pageNo == null || pageSize == null) {
            List<CheckinCheckoutResponse> checkinCheckoutResponses = checkInCheckOutRepository.findByUserAndDayWorkingBetween(user, startDate, endDate, Pageable.unpaged())
                    .stream()
                    .map(checkInCheckOut -> mapper.toResponse(checkInCheckOut))
                    .collect(Collectors.toList());

            CheckinCheckoutPage checkinCheckoutPage = CheckinCheckoutPage.builder()
                    .checkinCheckoutResponseList(checkinCheckoutResponses)
                    .pageNo(0)
                    .pageSize(checkinCheckoutResponses.size())
                    .totalPages(1)
                    .totalElements(checkinCheckoutResponses.size())
                    .last(true)
                    .build();
            log.info("Cache checkincheckout: get user record service unpaged");
            return checkinCheckoutPage;
        } else {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<CheckInCheckOut> checkinCheckoutPageData = checkInCheckOutRepository.findByUserAndDayWorkingBetween(user, startDate, endDate, pageable);

            List<CheckinCheckoutResponse> checkinCheckoutResponses = checkinCheckoutPageData.getContent()
                    .stream()
                    .map(checkInCheckOut -> mapper.toResponse(checkInCheckOut))
                    .collect(Collectors.toList());

            CheckinCheckoutPage checkinCheckoutPage = CheckinCheckoutPage.builder()
                    .checkinCheckoutResponseList(checkinCheckoutResponses)
                    .pageNo(pageable.getPageNumber())
                    .pageSize(pageSize)
                    .totalElements((int) checkinCheckoutPageData.getTotalElements())
                    .totalPages(checkinCheckoutPageData.getTotalPages())
                    .last(checkinCheckoutPageData.isLast())
                    .build();
            log.info("Cache checkincheckout: get user record service paged");
            return checkinCheckoutPage;
        }
    }


    @Override
    public CheckinCheckoutPage getAllRecordsByDayWorkingBetween(LocalDate startDate, LocalDate endDate, Integer pageNo, Integer pageSize) {
        if (startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            DayOfWeek dayOfWeek = today.getDayOfWeek();
            LocalDate startOfWeek = today.minusDays(dayOfWeek.getValue() - 1);
            LocalDate endOfWeek = today.plusDays(7 - dayOfWeek.getValue());
            startDate = startOfWeek;
            endDate = endOfWeek;
        }

        if (pageNo == null || pageSize == null) {
            List<CheckinCheckoutResponse> checkinCheckoutResponses = checkInCheckOutRepository.findByDayWorkingBetween(startDate, endDate, Pageable.unpaged())
                    .stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());

            CheckinCheckoutPage checkinCheckoutPage = CheckinCheckoutPage.builder()
                    .checkinCheckoutResponseList(checkinCheckoutResponses)
                    .pageNo(0)
                    .pageSize(checkinCheckoutResponses.size())
                    .totalPages(1)
                    .totalElements(checkinCheckoutResponses.size())
                    .last(true)
                    .build();
            log.info("Cache checkincheckout: get all records by day working between unpaged");
            return checkinCheckoutPage;
        } else {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<CheckInCheckOut> checkinCheckoutPageData = checkInCheckOutRepository.findByDayWorkingBetween(startDate, endDate, pageable);

            List<CheckinCheckoutResponse> checkinCheckoutResponses = checkinCheckoutPageData.getContent()
                    .stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());

            CheckinCheckoutPage checkinCheckoutPage = CheckinCheckoutPage.builder()
                    .checkinCheckoutResponseList(checkinCheckoutResponses)
                    .pageNo(pageable.getPageNumber())
                    .pageSize(pageSize)
                    .totalElements((int) checkinCheckoutPageData.getTotalElements())
                    .totalPages(checkinCheckoutPageData.getTotalPages())
                    .last(checkinCheckoutPageData.isLast())
                    .build();
            log.info("Cache checkincheckout: get all records by day working between paged");
            return checkinCheckoutPage;
        }
    }

    @Override
    public CheckinCheckoutPage getMonthlyCheckinErrors(LocalDate startDate, LocalDate endDate, Integer pageNo, Integer pageSize) {
        if (startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            DayOfWeek dayOfWeek = today.getDayOfWeek();
            LocalDate startOfWeek = today.minusDays(dayOfWeek.getValue() - 1);
            LocalDate endOfWeek = today.plusDays(7 - dayOfWeek.getValue());
            startDate = startOfWeek;
            endDate = endOfWeek;
        }

        if (pageNo == null || pageSize == null) {
            List<CheckinCheckoutResponse> checkinCheckoutResponses = checkInCheckOutRepository.findByDayWorkingBetween(startDate, endDate, Pageable.unpaged())
                    .stream()
                    .filter(record -> record.isCheckinLate() || record.isCheckoutEarly())
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());

            CheckinCheckoutPage checkinCheckoutPage = CheckinCheckoutPage.builder()
                    .checkinCheckoutResponseList(checkinCheckoutResponses)
                    .pageNo(0)
                    .pageSize(checkinCheckoutResponses.size())
                    .totalPages(1)
                    .totalElements(checkinCheckoutResponses.size())
                    .last(true)
                    .build();
            log.info("Cache checkincheckout: get monthly checkin errors unpaged");
            return checkinCheckoutPage;
        } else {
            Pageable pageable = PageRequest.of(pageNo, pageSize);

            List<CheckinCheckoutResponse> checkinCheckoutResponses = checkInCheckOutRepository.findByDayWorkingBetween(startDate, endDate, Pageable.unpaged())
                    .stream()
                    .filter(record -> record.isCheckinLate() || record.isCheckoutEarly())
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), checkinCheckoutResponses.size());
            List<CheckinCheckoutResponse> pageContent = checkinCheckoutResponses.subList(start, end);
            Page<CheckinCheckoutResponse> filteredPage = new PageImpl<>(pageContent, pageable, checkinCheckoutResponses.size());
            System.out.println(start);
            System.out.println(end);
            System.out.println(pageContent.size());
            CheckinCheckoutPage checkinCheckoutPage = CheckinCheckoutPage.builder()
                    .checkinCheckoutResponseList(filteredPage.getContent())
                    .pageNo(filteredPage.getNumber())
                    .pageSize(filteredPage.getSize())
                    .totalElements(filteredPage.getTotalElements())
                    .totalPages(filteredPage.getTotalPages())
                    .last(filteredPage.isLast())
                    .build();
            log.info("get monthly checkin errors paged");
            return checkinCheckoutPage;
        }
    }

    @Override
    public CheckinCheckoutPage getMonthlyCheckinErrorsForUser(Integer userId, LocalDate startDate, LocalDate endDate, Integer pageNo, Integer pageSize) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (pageNo == null || pageSize == null) {
            List<CheckinCheckoutResponse> checkinCheckoutResponses = checkInCheckOutRepository.findByUserAndDayWorkingBetween(user, startDate, endDate, Pageable.unpaged())
                    .stream()
                    .filter(record -> record.isCheckinLate() || record.isCheckoutEarly())
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());

            CheckinCheckoutPage checkinCheckoutPage = CheckinCheckoutPage.builder()
                    .checkinCheckoutResponseList(checkinCheckoutResponses)
                    .pageNo(0)
                    .pageSize(checkinCheckoutResponses.size())
                    .totalPages(1)
                    .totalElements(checkinCheckoutResponses.size())
                    .last(true)
                    .build();
            log.info("Cache checkincheckout: get monthly checkin errors for user unpaged");
            return checkinCheckoutPage;
        } else {
            Pageable pageable = PageRequest.of(pageNo, pageSize);

            List<CheckinCheckoutResponse> checkinCheckoutResponses = checkInCheckOutRepository.findByUserAndDayWorkingBetween(user, startDate, endDate, Pageable.unpaged())
                    .stream()
                    .filter(record -> record.isCheckinLate() || record.isCheckoutEarly())
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), checkinCheckoutResponses.size());
            List<CheckinCheckoutResponse> pageContent = checkinCheckoutResponses.subList(start, end);
            Page<CheckinCheckoutResponse> filteredPage = new PageImpl<>(pageContent, pageable, checkinCheckoutResponses.size());

            CheckinCheckoutPage checkinCheckoutPage = CheckinCheckoutPage.builder()
                    .checkinCheckoutResponseList(filteredPage.getContent())
                    .pageNo(filteredPage.getNumber())
                    .pageSize(filteredPage.getSize())
                    .totalElements(filteredPage.getTotalElements())
                    .totalPages(filteredPage.getTotalPages())
                    .last(filteredPage.isLast())
                    .build();
            log.info("get monthly checkin errors for user paged");
            return checkinCheckoutPage;
        }
    }


}