package com.familyhealth.api.event;

import com.familyhealth.api.model.Child;
import com.familyhealth.api.service.ReminderService;
import com.familyhealth.api.service.model.ReminderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BirthdayReminderListener {

    private final ReminderService reminderService;

    @EventListener
    public void onChildCreated(ChildCreatedEvent event) {
        Child child = event.child();
        reminderService.createReminder(
                child.getId(),
                new ReminderCommand(
                        child.getFirstName() + "'s first birthday",
                        child.getDateOfBirth().plusYears(1),
                        null
                )
        );
        log.info("Birthday reminder created for child '{}' (due {})",
                child.getFirstName(), child.getDateOfBirth().plusYears(1));
    }
}
