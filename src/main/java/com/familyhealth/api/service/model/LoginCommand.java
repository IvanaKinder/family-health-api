package com.familyhealth.api.service.model;

public record LoginCommand(
        String email,
        String password
) {}
