package com.ju17th.instagramcloneapi.controllers;

import com.ju17th.instagramcloneapi.payload.user.ChangePasswordRequest;
import com.ju17th.instagramcloneapi.payload.user.LoginRequest;
import com.ju17th.instagramcloneapi.payload.user.SignUpRequest;
import com.ju17th.instagramcloneapi.service.AuthService;
import com.ju17th.instagramcloneapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @ModelAttribute SignUpRequest signUpRequest,
                                          @RequestParam("image") MultipartFile image) {
        return authService.registerUser(signUpRequest, image);
    }

    @PostMapping("/password/change")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return userService.changeUserPassword(changePasswordRequest);
    }
}
