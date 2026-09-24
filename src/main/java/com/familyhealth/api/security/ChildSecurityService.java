package com.familyhealth.api.security;

import com.familyhealth.api.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("childSecurity")
@RequiredArgsConstructor
public class ChildSecurityService {

    private final ChildRepository childRepository;

    public boolean isOwner(Long childId, String email) {
        return childRepository.existsByIdAndUser_Email(childId, email);
    }
}
