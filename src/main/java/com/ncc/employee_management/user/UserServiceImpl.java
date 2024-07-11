package com.ncc.employee_management.user;

import com.ncc.employee_management.checkinCheckout.CheckInCheckOut;
import com.ncc.employee_management.checkinCheckout.CheckInCheckOutRepository;
import com.ncc.employee_management.config.JwtService;
import com.ncc.employee_management.email.EmailSenderService;
import com.ncc.employee_management.user.dto.UserCheckinCheckouDto;
import com.ncc.employee_management.validators.ObjectsValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.ncc.employee_management.auth.AuthenticationService.generateCheckinCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ObjectsValidator<UserRequest> validator;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailSenderService emailSenderService;
    private final CheckInCheckOutRepository checkInCheckOutRepository;

    @Transactional
    public UserResponse create(UserRequest request) {
        validator.validate(request);
        var user = mapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCheckinCode(generateCheckinCode());
        user.setRole(Role.USER);

        var savedUser = userRepository.save(user);

        String toMail = savedUser.getEmail();
        String subject = "Welcome to the Company";
        String body = "Your check-in code is: " + user.getCheckinCode();

        emailSenderService.sendSimpleEmail(toMail, subject, body);

        var userResponse = mapper.toResponse(savedUser);
        return userResponse;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(User::getFirstname))
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse findById(Integer id) {
        User user = userRepository.findById(id).get();
        UserResponse userResponse = mapper.toResponse(user);
//        System.out.println(user);
        log.info("before 68");
        System.out.println(user.getCheckInCheckOutList());
        log.info("before70");
        System.out.println(user.getTokens());
        return userResponse;
//        return userRepository.findById(id)
//                .map(mapper::toResponse)
//                .orElseThrow(() -> new EntityNotFoundException("No user found with the ID:: " + id));
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserResponse> findByFirstname(String firstname) {
        return userRepository.findByFirstnameContaining(firstname)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<UserCheckinCheckouDto> getAllEmployeesWithCheckInCheckOut(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            DayOfWeek dayOfWeek = today.getDayOfWeek();
            LocalDate startOfWeek = today.minusDays(dayOfWeek.getValue() - 1);
            LocalDate endOfWeek = today.plusDays(7 - dayOfWeek.getValue());
            startDate = startOfWeek;
            endDate = endOfWeek;
        }

        List<User> users = userRepository.findAll();

        List<UserCheckinCheckouDto> userCheckinCheckouDtoList = new ArrayList<>();

        LocalDate finalStartDate = startDate;
        LocalDate finalEndDate = endDate;

        users.forEach(user -> {
            Page<CheckInCheckOut> checkInCheckOutList = checkInCheckOutRepository.findByUserAndDayWorkingBetween(user, finalStartDate, finalEndDate, Pageable.unpaged());
            user.setCheckInCheckOutList(checkInCheckOutList.toList());
            userCheckinCheckouDtoList.add(mapper.toResponseWithCheckinCheckout(user));
        });

        return userCheckinCheckouDtoList;
    }

}
