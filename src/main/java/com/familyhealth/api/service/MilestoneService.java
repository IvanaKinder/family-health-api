package com.familyhealth.api.service;

import com.familyhealth.api.generated.model.MilestonePage;
import com.familyhealth.api.generated.model.MilestoneRequest;
import com.familyhealth.api.generated.model.MilestoneResponse;
import org.springframework.data.domain.Pageable;

public interface MilestoneService {

    MilestonePage listMilestones(Long childId, Pageable pageable);

    MilestoneResponse getMilestone(Long childId, Long milestoneId);

    MilestoneResponse createMilestone(Long childId, MilestoneRequest request);

    MilestoneResponse updateMilestone(Long childId, Long milestoneId, MilestoneRequest request);

    void deleteMilestone(Long childId, Long milestoneId);
}
