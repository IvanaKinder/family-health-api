package com.familyhealth.api.service.model;

import java.time.LocalDate;

public record ChildView(
        Long id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String bloodType,
        String socialSecurityNumber
) {}
