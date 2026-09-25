package com.familyhealth.api.controller;

import com.familyhealth.api.generated.api.RemindersApiDelegate;
import com.familyhealth.api.generated.model.ReminderPage;
import com.familyhealth.api.generated.model.ReminderRequest;
import com.familyhealth.api.generated.model.ReminderResponse;
import com.familyhealth.api.mapper.ReminderMapper;
import com.familyhealth.api.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RemindersApiDelegateImpl implements RemindersApiDelegate {

    private final ReminderService reminderService;
    private final ReminderMapper reminderMapper;

    @Override
    @PreAuthorize("@childSecurity.isOwner(#childId, authentication.name)")
    public ResponseEntity<ReminderPage> listReminders(Long childId, LocalDate fromDate, LocalDate toDate, Integer page, Integer size) {
        return ResponseEntity.ok(reminderMapper.toPage(reminderService.listReminders(childId, fromDate, toDate, PageRequest.of(page, size))));
    }

    @Override
    @PreAuthorize("@childSecurity.isOwner(#childId, authentication.name)")
    public ResponseEntity<ReminderResponse> createReminder(Long childId, ReminderRequest reminderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reminderMapper.toResponse(reminderService.createReminder(childId, reminderMapper.toCommand(reminderRequest))));
    }

    @Override
    @PreAuthorize("@reminderSecurity.isOwner(#reminderId, #childId, authentication.name)")
    public ResponseEntity<ReminderResponse> updateReminder(Long childId, Long reminderId, ReminderRequest reminderRequest) {
        return ResponseEntity.ok(reminderMapper.toResponse(reminderService.updateReminder(childId, reminderId, reminderMapper.toCommand(reminderRequest))));
    }

    @Override
    @PreAuthorize("@reminderSecurity.isOwner(#reminderId, #childId, authentication.name)")
    public ResponseEntity<Void> deleteReminder(Long childId, Long reminderId) {
        reminderService.deleteReminder(childId, reminderId);
        return ResponseEntity.noContent().build();
    }
}
