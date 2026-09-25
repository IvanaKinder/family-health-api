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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    @Transactional
    @PreAuthorize("@childSecurity.isOwner(#childId, authentication.name)")
    public ReminderResponse createReminder(Long childId, ReminderRequest request) {
        log.info("Creating reminder '{}' for child id: {}", request.getTitle(), childId);
        Reminder reminder = reminderMapper.toEntity(request);
        reminder.setChild(childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Child", childId)));
        return reminderMapper.toResponse(reminderRepository.save(reminder));
    }

    @Override
    @Transactional
    @PreAuthorize("@reminderSecurity.isOwner(#reminderId, #childId, authentication.name)")
    public ReminderResponse updateReminder(Long childId, Long reminderId, ReminderRequest request) {
        log.info("Updating reminder id: {}", reminderId);
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", reminderId));
        reminderMapper.updateEntity(request, reminder);
        return reminderMapper.toResponse(reminderRepository.save(reminder));
    }

    @Override
    @Transactional
    @PreAuthorize("@reminderSecurity.isOwner(#reminderId, #childId, authentication.name)")
    public void deleteReminder(Long childId, Long reminderId) {
        log.info("Deleting reminder id: {}", reminderId);
        reminderRepository.delete(reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", reminderId)));
    }
}
