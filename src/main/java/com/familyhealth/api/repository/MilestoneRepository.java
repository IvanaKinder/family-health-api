package com.familyhealth.api.repository;

import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findAllByChild(Child child);

    Optional<Milestone> findByIdAndChild(Long id, Child child);
}
