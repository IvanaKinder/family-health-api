package com.familyhealth.api.event;

import com.familyhealth.api.model.Child;
import com.familyhealth.api.service.ReminderService;
import com.familyhealth.api.service.model.ReminderCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BirthdayReminderListenerTest {

    @Mock private ReminderService reminderService;
    @InjectMocks private BirthdayReminderListener listener;

    @Test
    void onChildCreated_savesFirstBirthdayReminder() {
        Child child = Child.builder()
                .id(1L)
                .firstName("Emma")
                .dateOfBirth(LocalDate.of(2020, 3, 10))
                .build();

        listener.onChildCreated(new ChildCreatedEvent(child));

        ArgumentCaptor<ReminderCommand> captor = ArgumentCaptor.forClass(ReminderCommand.class);
        verify(reminderService).createReminder(eq(1L), captor.capture());

        ReminderCommand saved = captor.getValue();
        assertThat(saved.dueDate()).isEqualTo(LocalDate.of(2021, 3, 10));
        assertThat(saved.title()).contains("Emma");
    }
}
