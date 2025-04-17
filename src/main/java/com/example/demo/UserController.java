package com.example.demo;

import com.example.demo.service.login.GoogleOAuthLogin;
import com.example.demo.service.login.PhoneOtpLogin;
import com.example.demo.service.login.UsernamePasswordLogin;
import com.example.demo.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired private UserService userService;

  @PostMapping("/signup")
  public User signup(@RequestBody User user) {
    return userService.createUser(user);
  }

    @PostMapping("/login")
    public String login(@RequestBody Object loginRequest) {
        if (loginRequest instanceof UsernamePasswordLogin) {
            UsernamePasswordLogin login = (UsernamePasswordLogin) loginRequest;
            return userService.validateUser(login.getUsername(), login.getPassword());
        } else if (loginRequest instanceof PhoneOtpLogin) {
            PhoneOtpLogin login = (PhoneOtpLogin) loginRequest;
            User user = userService.validateUserOTP(login.getPhoneNumber(), login.getOtp());
            if (user != null) {
                return "success";
            }
            return "failed";
        } else if (loginRequest instanceof GoogleOAuthLogin) {
            // Implement Google OAuth verification here
            return "Google OAuth login not implemented yet";
        }
        return "Invalid credentials";
    }
  }