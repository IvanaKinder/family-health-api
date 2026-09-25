package com.familyhealth.api.event;

import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.Reminder;
import com.familyhealth.api.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BirthdayReminderListener {

    private final ReminderRepository reminderRepository;

    @EventListener
    public void onChildCreated(ChildCreatedEvent event) {
        Child child = event.child();
        reminderRepository.save(Reminder.builder()
                .child(child)
                .title(child.getFirstName() + "'s first birthday")
                .dueDate(child.getDateOfBirth().plusYears(1))
                .build());
    }
}
