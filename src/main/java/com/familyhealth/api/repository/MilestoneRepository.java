package com.familyhealth.api.repository;

import com.familyhealth.api.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findAllByChildId(Long childId);

    boolean existsByIdAndChild_IdAndChild_User_Email(Long id, Long childId, String email);
}
