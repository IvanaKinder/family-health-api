package com.familyhealth.api.service;

import com.familyhealth.api.service.model.MilestoneCommand;
import com.familyhealth.api.service.model.MilestoneView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MilestoneService {

    Page<MilestoneView> listMilestones(Long childId, Pageable pageable);

    MilestoneView getMilestone(Long childId, Long milestoneId);

    MilestoneView createMilestone(Long childId, MilestoneCommand command);

    MilestoneView updateMilestone(Long childId, Long milestoneId, MilestoneCommand command);

    void deleteMilestone(Long childId, Long milestoneId);
}
