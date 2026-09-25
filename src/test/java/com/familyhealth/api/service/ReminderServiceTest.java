package com.familyhealth.api.service;

import com.familyhealth.api.exception.ResourceNotFoundException;
import com.familyhealth.api.generated.model.ReminderRequest;
import com.familyhealth.api.generated.model.ReminderResponse;
import com.familyhealth.api.mapper.ReminderMapper;
import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.Reminder;
import com.familyhealth.api.model.User;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.ReminderRepository;
import com.familyhealth.api.specification.ReminderSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private ReminderRequest reminderRequest;

    @BeforeEach
    void setUp() {
        reminderService = new ReminderServiceImpl(reminderRepository, reminderMapper, childRepository);

        User user = User.builder().id(1L).email("user@example.com").build();
        child = Child.builder().id(10L).user(user).firstName("Emma").build();
        reminder = Reminder.builder().id(30L).child(child).title("Dentist").dueDate(LocalDate.of(2024, 3, 15)).build();
        reminderRequest = new ReminderRequest().title("Dentist").dueDate(LocalDate.of(2024, 3, 15));
    }

    @Test
    void listReminders_noFilter_returnsAllRemindersForChild() {
        Pageable pageable = PageRequest.of(0, 20);
        when(reminderRepository.findAll(any(ReminderSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(reminder), pageable, 1));

        var result = reminderService.listReminders(10L, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Dentist");
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void createReminder_savesWithChildAndReturnsResponse() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));
        when(reminderRepository.save(any(Reminder.class))).thenAnswer(inv -> {
            Reminder saved = inv.getArgument(0);
            saved.setId(30L);
            return saved;
        });

        ReminderResponse result = reminderService.createReminder(10L, reminderRequest);

        assertThat(result.getTitle()).isEqualTo("Dentist");
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void updateReminder_existingId_updatesFields() {
        when(reminderRepository.findById(30L)).thenReturn(Optional.of(reminder));
        when(reminderRepository.save(reminder)).thenReturn(reminder);

        ReminderRequest updateRequest = new ReminderRequest().title("Eye doctor").dueDate(LocalDate.of(2024, 4, 1));
        ReminderResponse result = reminderService.updateReminder(10L, 30L, updateRequest);

        assertThat(result.getTitle()).isEqualTo("Eye doctor");
        verify(reminderRepository).save(reminder);
    }

    @Test
    void updateReminder_reminderNotFound_throwsResourceNotFoundException() {
        when(reminderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reminderService.updateReminder(10L, 999L, reminderRequest))
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
