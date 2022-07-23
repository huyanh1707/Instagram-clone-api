package com.ju17th.instagramcloneapi.service.impl;

import com.ju17th.instagramcloneapi.exception.AppException;
import com.ju17th.instagramcloneapi.model.user.Role;
import com.ju17th.instagramcloneapi.model.user.RoleName;
import com.ju17th.instagramcloneapi.model.user.User;
import com.ju17th.instagramcloneapi.payload.ApiResponse;
import com.ju17th.instagramcloneapi.payload.JwtAuthenticationResponse;
import com.ju17th.instagramcloneapi.payload.user.LoginRequest;
import com.ju17th.instagramcloneapi.payload.user.SignUpRequest;
import com.ju17th.instagramcloneapi.repository.RoleRepository;
import com.ju17th.instagramcloneapi.repository.UserRepository;
import com.ju17th.instagramcloneapi.security.JwtTokenProvider;
import com.ju17th.instagramcloneapi.service.AuthService;
import com.ju17th.instagramcloneapi.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public ResponseEntity<?> registerUser(SignUpRequest signUpRequest, MultipartFile image){
        if (userRepository.existsByUsername(signUpRequest.getUsername()))
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"), HttpStatus.BAD_REQUEST);

        if (userRepository.existsByEmail(signUpRequest.getEmail()))
            return new ResponseEntity(new ApiResponse(false, "Email Adress is already in use!"), HttpStatus.BAD_REQUEST);

        String fileName = fileStorageService.storeFile(image);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api/posts/images/")
                .path(fileName)
                .toUriString();

        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword(), fileDownloadUri);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User Role not set."));
        user.setRoles(Collections.singleton(userRole));

        User result = userRepository.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/users/{username}").buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully."));
    }

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}
