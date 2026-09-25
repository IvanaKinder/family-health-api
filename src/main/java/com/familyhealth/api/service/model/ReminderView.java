package com.familyhealth.api.service.model;

import java.time.LocalDate;

public record ReminderView(
        Long id,
        String title,
        LocalDate dueDate,
        String description
) {}
