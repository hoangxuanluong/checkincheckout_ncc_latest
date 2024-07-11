package com.ncc.employee_management.user;

import com.ncc.employee_management.config.JwtService;
import com.ncc.employee_management.email.EmailSenderService;
import com.ncc.employee_management.validators.ObjectsValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ObjectsValidator<UserRequest> validator;
    @Mock
    private UserMapper mapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void should_create_user() {
        //UNIT TEST
        //1. create mock data
        //2. define behavior
        //3. call service method
        // 4. assert the result
        //// 4.1 ensure METHOD is called

        //GIVEN
        UserRequest userRequest = UserRequest.builder()
                .id(1)
                .firstname("luong")
                .lastname("hoang")
                .email("hxluong1611@gmail.com")
                .password(passwordEncoder.encode("123456"))
                .build();

        User user = User.builder()
                .id(1)
                .firstname("luong")
                .lastname("hoang")
                .email("hxluong1611@gmail.com")
                .password(passwordEncoder.encode("123456"))
                .role(Role.USER)
                .checkinCode("1234")
                .build();

        UserResponse userResponseExpect = UserResponse.builder()
                .id(1)
                .firstname("luong")
                .lastname("hoang")
                .email("hxluong1611@gmail.com")
                .build();


        //MOCK THE CALLS
        when(mapper.toUser(userRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(userResponseExpect);

        //WHEN
        UserResponse userResponse = userService.create(userRequest);
        System.out.println(userResponse);

        verify(validator, times(1)).validate(userRequest);
        verify(mapper).toUser(userRequest);
        verify(passwordEncoder).encode(userRequest.getPassword());
        verify(userRepository).save(user);

        //THEN
        assertThat(userResponse).isEqualTo(userResponseExpect);
    }

    @Test
    @Disabled
    void findAll() {
    }

    @Test
    void findById_UserExists_ReturnsUserResponse() {
        //GIVEN
        Integer id = 1;

        User user = User.builder()
                .id(1)
                .firstname("luong")
                .lastname("hoang")
                .email("hxluong1611@gmail.com")
                .password(passwordEncoder.encode("123456"))
                .role(Role.USER)
                .checkinCode("1234")
                .build();

        UserResponse userResponseExpect = UserResponse.builder()
                .id(1)
                .firstname("luong")
                .lastname("hoang")
                .email("hxluong1611@gmail.com")
                .build();

        //MOCK THE CALL
//        when(userRepository.findById(id)
//                .map(mapper::toResponse)
//                .orElseThrow(() -> new EntityNotFoundException("No user found with the ID:: " + id)))
//                .thenReturn(userResponseExpect);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(mapper.toResponse(user)).thenReturn(userResponseExpect);


        //WHEN
        UserResponse userResponse = userService.findById(id);

        verify(userRepository, times(1)).findById(id);

        //THEN
        assertThat(userResponse).isNotNull();
        assertThat(userResponseExpect).isEqualTo(userResponse);


    }

    @Test
    void findById_UserNotExist_ThrowEntityNotFoundException() {

        Integer id = 1;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class, () -> userService.findById(id));
        assertThat("No user found with the ID:: " + id).isEqualTo(exception.getMessage());
    }

    @Test
    @Disabled
    void delete() {

    }
}