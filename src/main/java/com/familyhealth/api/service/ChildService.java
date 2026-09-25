package com.familyhealth.api.service;

import com.familyhealth.api.exception.ResourceNotFoundException;
import com.familyhealth.api.generated.model.ChildPage;
import com.familyhealth.api.generated.model.ChildRequest;
import com.familyhealth.api.generated.model.ChildResponse;
import com.familyhealth.api.mapper.ChildMapper;
import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.User;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final ChildMapper childMapper;
    private final UserRepository userRepository;

    public ChildPage listChildren(Pageable pageable) {
        return childMapper.toPage(childRepository.findAllByUser(getCurrentUser(), pageable));
    }

    @PreAuthorize("@childSecurity.isOwner(#id, authentication.name)")
    public ChildResponse getChild(Long id) {
        return childMapper.toResponse(childRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Child", id)));
    }

    public ChildResponse createChild(ChildRequest request) {
        Child child = childMapper.toEntity(request);
        child.setUser(getCurrentUser());
        return childMapper.toResponse(childRepository.save(child));
    }

    @PreAuthorize("@childSecurity.isOwner(#id, authentication.name)")
    public ChildResponse updateChild(Long id, ChildRequest request) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Child", id));
        childMapper.updateEntity(request, child);
        return childMapper.toResponse(childRepository.save(child));
    }

    @PreAuthorize("@childSecurity.isOwner(#id, authentication.name)")
    public void deleteChild(Long id) {
        childRepository.delete(childRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Child", id)));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
