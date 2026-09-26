package com.familyhealth.api.service;

import com.familyhealth.api.service.model.ChildCommand;
import com.familyhealth.api.service.model.ChildView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChildService {

    Page<ChildView> listChildren(String userEmail, Pageable pageable);

    ChildView getChild(Long id);

    ChildView createChild(ChildCommand command);

    ChildView updateChild(Long id, ChildCommand command);

    void deleteChild(Long id);
}
