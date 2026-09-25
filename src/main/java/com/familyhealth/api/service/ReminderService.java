package com.familyhealth.api.service;

import com.familyhealth.api.generated.model.ReminderPage;
import com.familyhealth.api.generated.model.ReminderRequest;
import com.familyhealth.api.generated.model.ReminderResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReminderService {

    ReminderPage listReminders(Long childId, LocalDate fromDate, LocalDate toDate, Pageable pageable);

    ReminderResponse createReminder(Long childId, ReminderRequest request);

    ReminderResponse updateReminder(Long childId, Long reminderId, ReminderRequest request);

    void deleteReminder(Long childId, Long reminderId);
}
