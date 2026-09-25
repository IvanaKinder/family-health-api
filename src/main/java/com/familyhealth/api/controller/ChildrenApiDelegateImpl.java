package com.familyhealth.api.controller;

import com.familyhealth.api.generated.api.ChildrenApiDelegate;
import com.familyhealth.api.generated.model.ChildPage;
import com.familyhealth.api.generated.model.ChildRequest;
import com.familyhealth.api.generated.model.ChildResponse;
import com.familyhealth.api.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChildrenApiDelegateImpl implements ChildrenApiDelegate {

    private final ChildService childService;

    @Override
    public ResponseEntity<ChildPage> listChildren(Integer page, Integer size) {
        return ResponseEntity.ok(childService.listChildren(PageRequest.of(page, size)));
    }

    @Override
    public ResponseEntity<ChildResponse> createChild(ChildRequest childRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(childService.createChild(childRequest));
    }

    @Override
    public ResponseEntity<ChildResponse> getChild(Long childId) {
        return ResponseEntity.ok(childService.getChild(childId));
    }

    @Override
    public ResponseEntity<ChildResponse> updateChild(Long childId, ChildRequest childRequest) {
        return ResponseEntity.ok(childService.updateChild(childId, childRequest));
    }

    @Override
    public ResponseEntity<Void> deleteChild(Long childId) {
        childService.deleteChild(childId);
        return ResponseEntity.noContent().build();
    }
}
