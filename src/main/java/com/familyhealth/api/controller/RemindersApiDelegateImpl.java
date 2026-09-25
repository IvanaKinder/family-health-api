package com.familyhealth.api.controller;

import com.familyhealth.api.generated.api.RemindersApiDelegate;
import com.familyhealth.api.generated.model.ReminderRequest;
import com.familyhealth.api.generated.model.ReminderResponse;
import com.familyhealth.api.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RemindersApiDelegateImpl implements RemindersApiDelegate {

    private final ReminderService reminderService;

    @Override
    public ResponseEntity<List<ReminderResponse>> listReminders(Long childId, LocalDate fromDate, LocalDate toDate) {
        return ResponseEntity.ok(reminderService.listReminders(childId, fromDate, toDate));
    }

    @Override
    public ResponseEntity<ReminderResponse> createReminder(Long childId, ReminderRequest reminderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reminderService.createReminder(childId, reminderRequest));
    }

    @Override
    public ResponseEntity<ReminderResponse> updateReminder(Long childId, Long reminderId, ReminderRequest reminderRequest) {
        return ResponseEntity.ok(reminderService.updateReminder(childId, reminderId, reminderRequest));
    }

    @Override
    public ResponseEntity<Void> deleteReminder(Long childId, Long reminderId) {
        reminderService.deleteReminder(childId, reminderId);
        return ResponseEntity.noContent().build();
    }
}
