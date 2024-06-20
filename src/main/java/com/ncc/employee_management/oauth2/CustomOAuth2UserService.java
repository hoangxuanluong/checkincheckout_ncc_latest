package com.ncc.employee_management.oauth2;


import com.ncc.employee_management.entity.AuthProvider;
import com.ncc.employee_management.entity.User;
import com.ncc.employee_management.exception.BadRequestException;
import com.ncc.employee_management.exception.OAuth2AuthenticationProcessingException;
import com.ncc.employee_management.oauth2.user.OAuth2UserInfo;
import com.ncc.employee_management.oauth2.user.OAuth2UserInfoFactory;
import com.ncc.employee_management.oauth2.user.UserPrincipal;
import com.ncc.employee_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.ncc.employee_management.entity.Role.USER;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        System.out.println("--------------------------");
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            System.out.println("------------------------------");
            System.out.println(oAuth2User);
            System.out.println(oAuth2UserRequest);
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(
                        oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes()
                );
        System.out.println("before 52");
        System.out.println(oAuth2UserInfo.getEmail());
        System.out.println(oAuth2UserInfo.getId());
        System.out.println(oAuth2UserInfo.getFirstName());
        if (oAuth2UserInfo.getEmail() == null) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        System.out.println("before 57");
        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
//        System.out.println(userOptional.get());
        System.out.println("----------------------------");
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            System.out.println(user);
            System.out.println(oAuth2UserRequest.getClientRegistration());
            System.out.println(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
            System.out.println(user.getProvider().toString().toLowerCase());
            if (!user.getProvider().toString().toLowerCase().equalsIgnoreCase(String.valueOf(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId())))) {
                throw new BadRequestException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .provider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))
                .providerId(oAuth2UserInfo.getId())
                .name(oAuth2UserInfo.getName())
                .firstname(oAuth2UserInfo.getFirstName())
                .lastname(oAuth2UserInfo.getLastName())
                .email(oAuth2UserInfo.getEmail())
                .imageUrl(oAuth2UserInfo.getImageUrl())
                .role(USER)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }
}