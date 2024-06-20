package com.ncc.employee_management.projection.classBased;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    // the parameter names of its constructor must match the properties of the root entity class.

    private String firstname;
    private String lastname;
}
