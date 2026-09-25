package com.familyhealth.api.service.model;

public record RegisterCommand(
        String email,
        String password,
        String firstName,
        String lastName
) {}
