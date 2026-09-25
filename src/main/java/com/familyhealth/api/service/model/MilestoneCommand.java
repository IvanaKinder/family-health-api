package com.familyhealth.api.service.model;

import java.time.LocalDate;

public record MilestoneCommand(
        String title,
        LocalDate date,
        String notes
) {}
