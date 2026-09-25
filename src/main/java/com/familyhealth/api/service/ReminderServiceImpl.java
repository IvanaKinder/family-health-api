package com.familyhealth.api.service;

import com.familyhealth.api.exception.ResourceNotFoundException;
import com.familyhealth.api.generated.model.ReminderPage;
import com.familyhealth.api.generated.model.ReminderRequest;
import com.familyhealth.api.generated.model.ReminderResponse;
import com.familyhealth.api.mapper.ReminderMapper;
import com.familyhealth.api.model.Reminder;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.ReminderRepository;
import com.familyhealth.api.specification.ReminderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final ReminderMapper reminderMapper;
    private final ChildRepository childRepository;

    @Override
    @PreAuthorize("@childSecurity.isOwner(#childId, authentication.name)")
    public ReminderPage listReminders(Long childId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        return reminderMapper.toPage(reminderRepository.findAll(new ReminderSpecification(childId, fromDate, toDate), pageable));
    }

    @Override
    @PreAuthorize("@childSecurity.isOwner(#childId, authentication.name)")
    public ReminderResponse createReminder(Long childId, ReminderRequest request) {
        Reminder reminder = reminderMapper.toEntity(request);
        reminder.setChild(childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Child", childId)));
        return reminderMapper.toResponse(reminderRepository.save(reminder));
    }

    @Override
    @PreAuthorize("@reminderSecurity.isOwner(#reminderId, #childId, authentication.name)")
    public ReminderResponse updateReminder(Long childId, Long reminderId, ReminderRequest request) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", reminderId));
        reminderMapper.updateEntity(request, reminder);
        return reminderMapper.toResponse(reminderRepository.save(reminder));
    }

    @Override
    @PreAuthorize("@reminderSecurity.isOwner(#reminderId, #childId, authentication.name)")
    public void deleteReminder(Long childId, Long reminderId) {
        reminderRepository.delete(reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", reminderId)));
    }
}
