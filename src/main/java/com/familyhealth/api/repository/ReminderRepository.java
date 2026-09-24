package com.familyhealth.api.repository;

import com.familyhealth.api.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findAllByChildId(Long childId);

    boolean existsByIdAndChild_IdAndChild_User_Email(Long id, Long childId, String email);
}
