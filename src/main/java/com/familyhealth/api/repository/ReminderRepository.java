package com.familyhealth.api.repository;

import com.familyhealth.api.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReminderRepository extends JpaRepository<Reminder, Long>, JpaSpecificationExecutor<Reminder> {

    boolean existsByIdAndChild_IdAndChild_User_Email(Long id, Long childId, String email);
}
