package com.ncc.employee_management.demo;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping("/")
    public String getTokens(@RequestParam(name = "accessToken") String accessToken,
                            @RequestParam(name = "refreshToken") String refreshToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "Access Token: " + accessToken + "<br>" + "Refresh Token: " + refreshToken
                + "<br>" + "User: " + authentication.getName();
    }
}
