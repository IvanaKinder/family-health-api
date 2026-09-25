package com.familyhealth.api.repository;

import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildRepository extends JpaRepository<Child, Long> {

    Page<Child> findAllByUser(User user, Pageable pageable);

    boolean existsByIdAndUser_Email(Long id, String email);
}
