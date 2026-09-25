package com.familyhealth.api.service;

import com.familyhealth.api.generated.model.LoginRequest;
import com.familyhealth.api.generated.model.RegisterRequest;

public interface AuthService {

    String register(RegisterRequest request);

    String login(LoginRequest request);
}
