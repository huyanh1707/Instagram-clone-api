package com.ju17th.instagramcloneapi.service;

import com.ju17th.instagramcloneapi.payload.user.LoginRequest;
import com.ju17th.instagramcloneapi.payload.user.SignUpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface AuthService {
    ResponseEntity<?> registerUser(SignUpRequest signUpRequest, MultipartFile image);

    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);
}
