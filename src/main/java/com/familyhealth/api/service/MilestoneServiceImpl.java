package com.familyhealth.api.service;

import com.familyhealth.api.exception.ResourceNotFoundException;
import com.familyhealth.api.mapper.MilestoneMapper;
import com.familyhealth.api.model.Milestone;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.MilestoneRepository;
import com.familyhealth.api.service.model.MilestoneCommand;
import com.familyhealth.api.service.model.MilestoneView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MilestoneServiceImpl implements MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final MilestoneMapper milestoneMapper;
    private final ChildRepository childRepository;

    @Override
    public Page<MilestoneView> listMilestones(Long childId, Pageable pageable) {
        return milestoneRepository.findAllByChildId(childId, pageable).map(milestoneMapper::toView);
    }

    @Override
    public MilestoneView getMilestone(Long childId, Long milestoneId) {
        return milestoneMapper.toView(milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", milestoneId)));
    }

    @Override
    @Transactional
    public MilestoneView createMilestone(Long childId, MilestoneCommand command) {
        log.info("Creating milestone '{}' for child id: {}", command.title(), childId);
        Milestone milestone = milestoneMapper.toEntity(command);
        milestone.setChild(childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Child", childId)));
        return milestoneMapper.toView(milestoneRepository.save(milestone));
    }

    @Override
    @Transactional
    public MilestoneView updateMilestone(Long childId, Long milestoneId, MilestoneCommand command) {
        log.info("Updating milestone id: {}", milestoneId);
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", milestoneId));
        milestoneMapper.updateEntity(command, milestone);
        return milestoneMapper.toView(milestoneRepository.save(milestone));
    }

    @Override
    @Transactional
    public void deleteMilestone(Long childId, Long milestoneId) {
        log.info("Deleting milestone id: {}", milestoneId);
        milestoneRepository.delete(milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", milestoneId)));
    }
}
