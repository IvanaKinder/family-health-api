package com.familyhealth.api.controller;

import com.familyhealth.api.generated.api.ChildrenApiDelegate;
import com.familyhealth.api.generated.model.ChildPage;
import com.familyhealth.api.generated.model.ChildRequest;
import com.familyhealth.api.generated.model.ChildResponse;
import com.familyhealth.api.mapper.ChildMapper;
import com.familyhealth.api.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChildrenApiDelegateImpl implements ChildrenApiDelegate {

    private final ChildService childService;
    private final ChildMapper childMapper;

    @Override
    public ResponseEntity<ChildPage> listChildren(Integer page, Integer size) {
        return ResponseEntity.ok(childMapper.toPage(
                childService.listChildren(currentUserEmail(), PageRequest.of(page, size))));
    }

    @Override
    public ResponseEntity<ChildResponse> createChild(ChildRequest childRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(childMapper.toResponse(
                        childService.createChild(childMapper.toCommand(childRequest, currentUserEmail()))));
    }

    @Override
    @PreAuthorize("@childSecurity.isOwner(#childId, authentication.name)")
    public ResponseEntity<ChildResponse> getChild(Long childId) {
        return ResponseEntity.ok(childMapper.toResponse(childService.getChild(childId)));
    }

    @Override
    @PreAuthorize("@childSecurity.isOwner(#childId, authentication.name)")
    public ResponseEntity<ChildResponse> updateChild(Long childId, ChildRequest childRequest) {
        return ResponseEntity.ok(childMapper.toResponse(
                childService.updateChild(childId, childMapper.toCommand(childRequest))));
    }

    @Override
    @PreAuthorize("@childSecurity.isOwner(#childId, authentication.name)")
    public ResponseEntity<Void> deleteChild(Long childId) {
        childService.deleteChild(childId);
        return ResponseEntity.noContent().build();
    }

    private String currentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
