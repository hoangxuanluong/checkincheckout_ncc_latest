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
    public String getTokens(@RequestParam(name = "accessToken", required = false) String accessToken,
                            @RequestParam(name = "refreshToken", required = false) String refreshToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "Access Token: " + accessToken + "<br>" + "Refresh Token: " + refreshToken
                + "<br>" + "User: " + authentication.getName();
    }


//    @GetMapping("/")
//    public String getTokens(HttpServletRequest request) {
//        // Lấy các cookie từ request
//        Cookie[] cookies = request.getCookies();
//        String accessToken = null;
//        String refreshToken = null;
//
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("accessToken".equals(cookie.getName())) {
//                    accessToken = cookie.getValue();
//                } else if ("refreshToken".equals(cookie.getName())) {
//                    refreshToken = cookie.getValue();
//                }
//            }
//        }
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return "Access Token: " + accessToken + "<br>"
//                + "Refresh Token: " + refreshToken + "<br>"
//                + "User: " + authentication.getName();
//    }


}
