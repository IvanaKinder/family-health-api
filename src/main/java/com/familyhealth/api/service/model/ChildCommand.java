package com.familyhealth.api.service.model;

import java.time.LocalDate;

public record ChildCommand(
        String userEmail,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String bloodType,
        String socialSecurityNumber
) {}
