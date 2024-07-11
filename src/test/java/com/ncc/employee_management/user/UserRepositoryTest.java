package com.ncc.employee_management.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByEmail() {
        String email = "test@example.com";
        User user = User.builder()
                .id(1)
                .role(Role.USER)
                .checkinCode("1234")
                .firstname("Hoang")
                .lastname("Luong")
                .email("hxluong1611@gmail.com")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findByEmail(email);
        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testFindByFullname() {
        // Given
        String firstname = "John";
        String lastname = "Doe";
        User user = new User();
        user.setFirstname(firstname);
        user.setLastname(lastname);
        when(userRepository.findByFullname(firstname, lastname)).thenReturn(List.of(user));

        // When
        List<User> found = userRepository.findByFullname(firstname, lastname);

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getFirstname()).isEqualTo(firstname);
        assertThat(found.get(0).getLastname()).isEqualTo(lastname);
    }

    // Add more tests for other methods as needed
}
