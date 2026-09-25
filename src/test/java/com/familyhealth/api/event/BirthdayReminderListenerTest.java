package com.familyhealth.api.event;

import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.Reminder;
import com.familyhealth.api.repository.ReminderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BirthdayReminderListenerTest {

    @Mock private ReminderRepository reminderRepository;
    @InjectMocks private BirthdayReminderListener listener;

    @Test
    void onChildCreated_savesFirstBirthdayReminder() {
        Child child = Child.builder()
                .id(1L)
                .firstName("Emma")
                .dateOfBirth(LocalDate.of(2020, 3, 10))
                .build();

        listener.onChildCreated(new ChildCreatedEvent(child));

        ArgumentCaptor<Reminder> captor = ArgumentCaptor.forClass(Reminder.class);
        verify(reminderRepository).save(captor.capture());

        Reminder saved = captor.getValue();
        assertThat(saved.getDueDate()).isEqualTo(LocalDate.of(2021, 3, 10));
        assertThat(saved.getTitle()).contains("Emma");
        assertThat(saved.getChild()).isEqualTo(child);
    }
}
