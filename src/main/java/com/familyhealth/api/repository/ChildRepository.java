package com.familyhealth.api.repository;

import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child, Long> {

    List<Child> findAllByUser(User user);

    Optional<Child> findByIdAndUser(Long id, User user);
}
