package com.familyhealth.api.controller;

import com.familyhealth.api.generated.api.MilestonesApiDelegate;
import com.familyhealth.api.generated.model.MilestonePage;
import com.familyhealth.api.generated.model.MilestoneRequest;
import com.familyhealth.api.generated.model.MilestoneResponse;
import com.familyhealth.api.mapper.MilestoneMapper;
import com.familyhealth.api.service.MilestoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MilestonesApiDelegateImpl implements MilestonesApiDelegate {

    private final MilestoneService milestoneService;
    private final MilestoneMapper milestoneMapper;

    @Override
    @PreAuthorize("@childSecurity.isOwner(#childId, authentication.name)")
    public ResponseEntity<MilestonePage> listMilestones(Long childId, Integer page, Integer size) {
        return ResponseEntity.ok(milestoneMapper.toPage(milestoneService.listMilestones(childId, PageRequest.of(page, size))));
    }

    @Override
    @PreAuthorize("@childSecurity.isOwner(#childId, authentication.name)")
    public ResponseEntity<MilestoneResponse> createMilestone(Long childId, MilestoneRequest milestoneRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(milestoneMapper.toResponse(milestoneService.createMilestone(childId, milestoneMapper.toCommand(milestoneRequest))));
    }

    @Override
    @PreAuthorize("@milestoneSecurity.isOwner(#milestoneId, #childId, authentication.name)")
    public ResponseEntity<MilestoneResponse> updateMilestone(Long childId, Long milestoneId, MilestoneRequest milestoneRequest) {
        return ResponseEntity.ok(milestoneMapper.toResponse(milestoneService.updateMilestone(childId, milestoneId, milestoneMapper.toCommand(milestoneRequest))));
    }

    @Override
    @PreAuthorize("@milestoneSecurity.isOwner(#milestoneId, #childId, authentication.name)")
    public ResponseEntity<Void> deleteMilestone(Long childId, Long milestoneId) {
        milestoneService.deleteMilestone(childId, milestoneId);
        return ResponseEntity.noContent().build();
    }
}
