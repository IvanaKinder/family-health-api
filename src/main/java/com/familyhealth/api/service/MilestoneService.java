package com.familyhealth.api.service;

import com.familyhealth.api.exception.ResourceNotFoundException;
import com.familyhealth.api.generated.model.MilestoneRequest;
import com.familyhealth.api.generated.model.MilestoneResponse;
import com.familyhealth.api.mapper.MilestoneMapper;
import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.Milestone;
import com.familyhealth.api.model.User;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.MilestoneRepository;
import com.familyhealth.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final MilestoneMapper milestoneMapper;
    private final ChildRepository childRepository;
    private final UserRepository userRepository;

    public List<MilestoneResponse> listMilestones(Long childId) {
        return milestoneRepository.findAllByChild(findOwnedChild(childId)).stream()
                .map(milestoneMapper::toResponse)
                .toList();
    }

    public MilestoneResponse getMilestone(Long childId, Long milestoneId) {
        return milestoneMapper.toResponse(findOwnedMilestone(milestoneId, findOwnedChild(childId)));
    }

    public MilestoneResponse createMilestone(Long childId, MilestoneRequest request) {
        Milestone milestone = milestoneMapper.toEntity(request);
        milestone.setChild(findOwnedChild(childId));
        return milestoneMapper.toResponse(milestoneRepository.save(milestone));
    }

    public MilestoneResponse updateMilestone(Long childId, Long milestoneId, MilestoneRequest request) {
        Milestone milestone = findOwnedMilestone(milestoneId, findOwnedChild(childId));
        milestoneMapper.updateEntity(request, milestone);
        return milestoneMapper.toResponse(milestoneRepository.save(milestone));
    }

    public void deleteMilestone(Long childId, Long milestoneId) {
        milestoneRepository.delete(findOwnedMilestone(milestoneId, findOwnedChild(childId)));
    }

    private Child findOwnedChild(Long childId) {
        return childRepository.findByIdAndUser(childId, getCurrentUser())
                .orElseThrow(() -> new ResourceNotFoundException("Child", childId));
    }

    private Milestone findOwnedMilestone(Long milestoneId, Child child) {
        return milestoneRepository.findByIdAndChild(milestoneId, child)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", milestoneId));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
