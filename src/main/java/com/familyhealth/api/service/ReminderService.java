package com.familyhealth.api.service;

import com.familyhealth.api.service.model.ReminderCommand;
import com.familyhealth.api.service.model.ReminderView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReminderService {

    Page<ReminderView> listReminders(Long childId, LocalDate fromDate, LocalDate toDate, Pageable pageable);

    ReminderView createReminder(Long childId, ReminderCommand command);

    ReminderView updateReminder(Long childId, Long reminderId, ReminderCommand command);

    void deleteReminder(Long childId, Long reminderId);
}
