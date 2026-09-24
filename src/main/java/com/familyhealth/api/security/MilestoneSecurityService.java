package com.familyhealth.api.security;

import com.familyhealth.api.repository.MilestoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("milestoneSecurity")
@RequiredArgsConstructor
public class MilestoneSecurityService {

    private final MilestoneRepository milestoneRepository;

    public boolean isOwner(Long milestoneId, Long childId, String email) {
        return milestoneRepository.existsByIdAndChild_IdAndChild_User_Email(milestoneId, childId, email);
    }
}
