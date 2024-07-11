package com.ncc.employee_management.checkinCheckout;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ncc.employee_management.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInCheckOut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;

    private LocalDate dayWorking;

    private boolean isWorking;
    private boolean isCheckinLate;
    private boolean isCheckoutEarly;

}