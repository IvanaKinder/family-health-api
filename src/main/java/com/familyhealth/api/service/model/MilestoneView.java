package com.familyhealth.api.service.model;

import java.time.LocalDate;

public record MilestoneView(
        Long id,
        String title,
        LocalDate date,
        String notes
) {}
