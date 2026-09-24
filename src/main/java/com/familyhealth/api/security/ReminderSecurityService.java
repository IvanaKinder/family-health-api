package com.familyhealth.api.security;

import com.familyhealth.api.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("reminderSecurity")
@RequiredArgsConstructor
public class ReminderSecurityService {

    private final ReminderRepository reminderRepository;

    public boolean isOwner(Long reminderId, Long childId, String email) {
        return reminderRepository.existsByIdAndChild_IdAndChild_User_Email(reminderId, childId, email);
    }
}
