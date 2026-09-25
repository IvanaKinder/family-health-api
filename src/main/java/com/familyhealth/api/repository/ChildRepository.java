package com.familyhealth.api.repository;

import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildRepository extends JpaRepository<Child, Long> {

    List<Child> findAllByUser(User user);

    boolean existsByIdAndUser_Email(Long id, String email);
}
