package com.ncc.employee_management.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncc.employee_management.config.JwtService;
import com.ncc.employee_management.email.EmailSenderService;
import com.ncc.employee_management.token.Token;
import com.ncc.employee_management.token.TokenRepository;
import com.ncc.employee_management.token.TokenType;
import com.ncc.employee_management.user.User;
import com.ncc.employee_management.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

import static com.ncc.employee_management.user.Role.USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailSenderService emailSenderService;

    public static String generateCheckinCode() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .checkinCode(generateCheckinCode())
                .role(USER)
                .build();

        var savedUser = repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        System.out.println("Current thread: " + Thread.currentThread().getName());
        log.info("Current thread: " + Thread.currentThread().getName());

        String toMail = savedUser.getEmail();
        String subject = "Welcome to the Company";
        String body = "Your check-in code is: " + user.getCheckinCode();

        emailSenderService.sendSimpleEmail(toMail, subject, body);


        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        System.out.println(request);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        System.out.println(authentication.getDetails());
        System.out.println(authentication.getAuthorities());
//        System.out.println(authentication.getCredentials());
//        System.out.println(authentication.getPrincipal());
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        System.out.println(user.getEmail());
        System.out.println(user.getAuthorities());
        System.out.println(user.getRole().name());
        var jwtToken = jwtService.generateToken(user);
        System.out.println(jwtToken);
        var refreshToken = jwtService.generateRefreshToken(user);
        System.out.println(refreshToken);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        if (!tokenRepository.findByToken(token.getToken()).isPresent()) {
            tokenRepository.save(token);
        } else {
            System.out.println("duplicate!!!!!!!");
        }
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
