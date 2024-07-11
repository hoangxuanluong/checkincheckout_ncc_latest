package com.ncc.employee_management.oauth2;


import com.ncc.employee_management.config.AppProperties;
import com.ncc.employee_management.config.JwtService;
import com.ncc.employee_management.exception.ResourceNotFoundException;
import com.ncc.employee_management.oauth2.user.UserPrincipal;
import com.ncc.employee_management.token.Token;
import com.ncc.employee_management.token.TokenRepository;
import com.ncc.employee_management.token.TokenType;
import com.ncc.employee_management.user.User;
import com.ncc.employee_management.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;


@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    //    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AppProperties appProperties;
    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        System.out.println(targetUrl);
        log.info(targetUrl);
        targetUrl = "http://localhost:3000" + targetUrl;
        System.out.println(targetUrl);
        System.out.println(request);
        System.out.println(response);


        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

//        clearAuthenticationAttributes(request, response);

//        getRedirectStrategy().sendRedirect(request, response, targetUrl);

        // -----------------COOKIE-------------------------
        // Get UserPrincipal from Authentication
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Get User from UserPrincipal
        User user = userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userPrincipal.getEmail()));

        /// Generate JWT token
//        JwtToken jwtToken = jwtService.generateJwtToken(user);
        var jwtToken = jwtService.generateToken(user);
        System.out.println(jwtToken);
        var refreshToken = jwtService.generateRefreshToken(user);
        System.out.println(refreshToken);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        Cookie accessTokenCookie = new Cookie("accessToken", jwtToken);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);


        System.out.println(request.getMethod());
        System.out.println(request.getRequestURI());
        System.out.println(request.getServletPath());

//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        response.sendRedirect(targetUrl);

        //-----------------------------------------------------
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
//                .map(Cookie::getValue);
//
//        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
//            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
//        }

//        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        String targetUrl = "/";
        // Get UserPrincipal from Authentication
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Get User from UserPrincipal
        User user = userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userPrincipal.getEmail()));

        /// Generate JWT token
//        JwtToken jwtToken = jwtService.generateJwtToken(user);
        var jwtToken = jwtService.generateToken(user);
        System.out.println(jwtToken);
        var refreshToken = jwtService.generateRefreshToken(user);
        System.out.println(refreshToken);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", jwtToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
    }

//    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
//        super.clearAuthenticationAttributes(request);
//        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
//    }

//    private boolean isAuthorizedRedirectUri(String uri) {
//        URI clientRedirectUri = URI.create(uri);
//
//        return appProperties.getOauth2().getAuthorizedRedirectUris()
//                .stream()
//                .anyMatch(authorizedRedirectUri -> {
//                    // Only validate host and port. Let the clients use different paths if they want to
//                    URI authorizedURI = URI.create(authorizedRedirectUri);
//                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
//                            && authorizedURI.getPort() == clientRedirectUri.getPort();
//                });
//    }


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
}