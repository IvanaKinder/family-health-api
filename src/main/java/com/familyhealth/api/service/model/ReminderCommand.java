package com.familyhealth.api.service.model;

import java.time.LocalDate;

public record ReminderCommand(
        String title,
        LocalDate dueDate,
        String description
) {}
