package com.familyhealth.api.service;

import com.familyhealth.api.generated.model.ChildPage;
import com.familyhealth.api.generated.model.ChildRequest;
import com.familyhealth.api.generated.model.ChildResponse;
import org.springframework.data.domain.Pageable;

public interface ChildService {

    ChildPage listChildren(Pageable pageable);

    ChildResponse getChild(Long id);

    ChildResponse createChild(ChildRequest request);

    ChildResponse updateChild(Long id, ChildRequest request);

    void deleteChild(Long id);
}
