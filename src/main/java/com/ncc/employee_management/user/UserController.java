package com.ncc.employee_management.user;

import com.ncc.employee_management.user.dto.UserCheckinCheckouDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;


    @PostMapping
    public UserResponse save(
            @RequestBody UserRequest userRequest
    ) {
        return service.create(userRequest);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<UserResponse> findById(
            @PathVariable("user-id") Integer id
    ) {
        UserResponse userResponse = service.findById(id);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/firstname/{firstname}")
    public ResponseEntity<List<UserResponse>> findByFistname(
            @PathVariable("firstname") String firstname
    ) {
        return ResponseEntity.ok(service.findByFirstname(firstname));
    }

    @GetMapping("/with-checkincheckout")
    public List<UserCheckinCheckouDto> getAllEmployeesWithCheckInCheckOut(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return service.getAllEmployeesWithCheckInCheckOut(startDate, endDate);
    }
}