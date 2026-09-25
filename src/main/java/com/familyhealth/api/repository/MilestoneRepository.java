package com.familyhealth.api.repository;

import com.familyhealth.api.model.Milestone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    Page<Milestone> findAllByChildId(Long childId, Pageable pageable);

    boolean existsByIdAndChild_IdAndChild_User_Email(Long id, Long childId, String email);
}
