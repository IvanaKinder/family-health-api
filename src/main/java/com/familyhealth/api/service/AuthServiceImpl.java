package com.familyhealth.api.service;

import com.familyhealth.api.exception.DuplicateEmailException;
import com.familyhealth.api.mapper.UserMapper;
import com.familyhealth.api.model.User;
import com.familyhealth.api.repository.UserRepository;
import com.familyhealth.api.service.model.LoginCommand;
import com.familyhealth.api.service.model.RegisterCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public String register(RegisterCommand command) {
        log.info("Registering user: {}", command.email());
        if (userRepository.existsByEmail(command.email())) {
            throw new DuplicateEmailException(command.email());
        }

        User user = userMapper.toEntity(command);
        user.setPassword(passwordEncoder.encode(command.password()));

        userRepository.save(user);
        return jwtService.generateToken(user);
    }

    @Override
    public String login(LoginCommand command) {
        log.info("Login attempt: {}", command.email());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(command.email(), command.password())
        );

        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + command.email()));

        log.info("Login successful: {}", command.email());
        return jwtService.generateToken(user);
    }
}
