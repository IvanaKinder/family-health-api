package com.familyhealth.api.service;

import com.familyhealth.api.exception.ResourceNotFoundException;
import com.familyhealth.api.mapper.ReminderMapper;
import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.Reminder;
import com.familyhealth.api.model.User;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.ReminderRepository;
import com.familyhealth.api.service.model.ReminderCommand;
import com.familyhealth.api.service.model.ReminderView;
import com.familyhealth.api.specification.ReminderSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock private ReminderRepository reminderRepository;
    @Mock private ChildRepository childRepository;

    private final ReminderMapper reminderMapper = Mappers.getMapper(ReminderMapper.class);
    private ReminderServiceImpl reminderService;

    private Child child;
    private Reminder reminder;
    private ReminderCommand reminderCommand;

    @BeforeEach
    void setUp() {
        reminderService = new ReminderServiceImpl(reminderRepository, reminderMapper, childRepository);

        User user = User.builder().id(1L).email("user@example.com").build();
        child = Child.builder().id(10L).user(user).firstName("Emma").build();
        reminder = Reminder.builder().id(30L).child(child).title("Dentist").dueDate(LocalDate.of(2024, 3, 15)).build();
        reminderCommand = new ReminderCommand("Dentist", LocalDate.of(2024, 3, 15), null);
    }

    @Test
    void listReminders_noFilter_returnsAllRemindersForChild() {
        Pageable pageable = PageRequest.of(0, 20);
        when(reminderRepository.findAll(any(ReminderSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(reminder), pageable, 1));

        Page<ReminderView> result = reminderService.listReminders(10L, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().title()).isEqualTo("Dentist");
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void createReminder_savesWithChildAndReturnsView() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));
        when(reminderRepository.save(any(Reminder.class))).thenAnswer(inv -> {
            Reminder saved = inv.getArgument(0);
            saved.setId(30L);
            return saved;
        });

        ReminderView result = reminderService.createReminder(10L, reminderCommand);

        assertThat(result.title()).isEqualTo("Dentist");
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void updateReminder_existingId_updatesFields() {
        when(reminderRepository.findById(30L)).thenReturn(Optional.of(reminder));
        when(reminderRepository.save(reminder)).thenReturn(reminder);

        ReminderCommand updateCommand = new ReminderCommand("Eye doctor", LocalDate.of(2024, 4, 1), null);
        ReminderView result = reminderService.updateReminder(10L, 30L, updateCommand);

        assertThat(result.title()).isEqualTo("Eye doctor");
        verify(reminderRepository).save(reminder);
    }

    @Test
    void updateReminder_reminderNotFound_throwsResourceNotFoundException() {
        when(reminderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reminderService.updateReminder(10L, 999L, reminderCommand))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void deleteReminder_existingId_deletes() {
        when(reminderRepository.findById(30L)).thenReturn(Optional.of(reminder));

        reminderService.deleteReminder(10L, 30L);

        verify(reminderRepository).delete(reminder);
    }

    @Test
    void deleteReminder_reminderNotFound_throwsResourceNotFoundException() {
        when(reminderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reminderService.deleteReminder(10L, 999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
