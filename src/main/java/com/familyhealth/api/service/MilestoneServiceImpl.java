package com.familyhealth.api.service;

import com.familyhealth.api.exception.ResourceNotFoundException;
import com.familyhealth.api.generated.model.MilestonePage;
import com.familyhealth.api.generated.model.MilestoneRequest;
import com.familyhealth.api.generated.model.MilestoneResponse;
import com.familyhealth.api.mapper.MilestoneMapper;
import com.familyhealth.api.model.Milestone;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.MilestoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MilestoneServiceImpl implements MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final MilestoneMapper milestoneMapper;
    private final ChildRepository childRepository;

    @Override
    @PreAuthorize("@childSecurity.isOwner(#childId, authentication.name)")
    public MilestonePage listMilestones(Long childId, Pageable pageable) {
        return milestoneMapper.toPage(milestoneRepository.findAllByChildId(childId, pageable));
    }

    @Override
    @PreAuthorize("@childSecurity.isOwner(#childId, authentication.name)")
    public MilestoneResponse getMilestone(Long childId, Long milestoneId) {
        return milestoneMapper.toResponse(milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", milestoneId)));
    }

    @Override
    @Transactional
    @PreAuthorize("@childSecurity.isOwner(#childId, authentication.name)")
    public MilestoneResponse createMilestone(Long childId, MilestoneRequest request) {
        log.info("Creating milestone '{}' for child id: {}", request.getTitle(), childId);
        Milestone milestone = milestoneMapper.toEntity(request);
        milestone.setChild(childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Child", childId)));
        return milestoneMapper.toResponse(milestoneRepository.save(milestone));
    }

    @Override
    @Transactional
    @PreAuthorize("@milestoneSecurity.isOwner(#milestoneId, #childId, authentication.name)")
    public MilestoneResponse updateMilestone(Long childId, Long milestoneId, MilestoneRequest request) {
        log.info("Updating milestone id: {}", milestoneId);
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", milestoneId));
        milestoneMapper.updateEntity(request, milestone);
        return milestoneMapper.toResponse(milestoneRepository.save(milestone));
    }

    @Override
    @Transactional
    @PreAuthorize("@milestoneSecurity.isOwner(#milestoneId, #childId, authentication.name)")
    public void deleteMilestone(Long childId, Long milestoneId) {
        log.info("Deleting milestone id: {}", milestoneId);
        milestoneRepository.delete(milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", milestoneId)));
    }
}
