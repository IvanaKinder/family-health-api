package com.familyhealth.api.service;

import com.familyhealth.api.exception.ResourceNotFoundException;
import com.familyhealth.api.mapper.ReminderMapper;
import com.familyhealth.api.model.Reminder;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.ReminderRepository;
import com.familyhealth.api.service.model.ReminderCommand;
import com.familyhealth.api.service.model.ReminderView;
import com.familyhealth.api.specification.ReminderSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<ReminderView> listReminders(Long childId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        return reminderRepository.findAll(new ReminderSpecification(childId, fromDate, toDate), pageable)
                .map(reminderMapper::toView);
    }

    @Override
    @Transactional
    public ReminderView createReminder(Long childId, ReminderCommand command) {
        log.info("Creating reminder '{}' for child id: {}", command.title(), childId);
        Reminder reminder = reminderMapper.toEntity(command);
        reminder.setChild(childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Child", childId)));
        return reminderMapper.toView(reminderRepository.save(reminder));
    }

    @Override
    @Transactional
    public ReminderView updateReminder(Long childId, Long reminderId, ReminderCommand command) {
        log.info("Updating reminder id: {}", reminderId);
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", reminderId));
        reminderMapper.updateEntity(command, reminder);
        return reminderMapper.toView(reminderRepository.save(reminder));
    }

    @Override
    @Transactional
    public void deleteReminder(Long childId, Long reminderId) {
        log.info("Deleting reminder id: {}", reminderId);
        reminderRepository.delete(reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", reminderId)));
    }
}
