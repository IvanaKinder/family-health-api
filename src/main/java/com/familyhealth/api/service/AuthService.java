package com.familyhealth.api.service;

import com.familyhealth.api.service.model.LoginCommand;
import com.familyhealth.api.service.model.RegisterCommand;

public interface AuthService {

    String register(RegisterCommand command);

    String login(LoginCommand command);
}
