package com.familyhealth.api.service;

import com.familyhealth.api.exception.DuplicateEmailException;
import com.familyhealth.api.generated.model.LoginRequest;
import com.familyhealth.api.generated.model.RegisterRequest;
import com.familyhealth.api.mapper.UserMapper;
import com.familyhealth.api.model.User;
import com.familyhealth.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public String register(RegisterRequest request) {
        log.info("Registering user: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        return jwtService.generateToken(user);
    }

    @Override
    public String login(LoginRequest request) {
        log.info("Login attempt: {}", request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        log.info("Login successful: {}", request.getEmail());
        return jwtService.generateToken(user);
    }
}
