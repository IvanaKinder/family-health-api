package com.familyhealth.api.controller;

import com.familyhealth.api.generated.api.AuthApiDelegate;
import com.familyhealth.api.generated.model.AuthResponse;
import com.familyhealth.api.generated.model.LoginRequest;
import com.familyhealth.api.generated.model.RegisterRequest;
import com.familyhealth.api.mapper.UserMapper;
import com.familyhealth.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthApiDelegateImpl implements AuthApiDelegate {

    private final AuthService authService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<AuthResponse> register(RegisterRequest request) {
        String token = authService.register(userMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse().token(token).tokenType("Bearer"));
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        String token = authService.login(userMapper.toCommand(request));
        return ResponseEntity.ok(new AuthResponse().token(token).tokenType("Bearer"));
    }
}
