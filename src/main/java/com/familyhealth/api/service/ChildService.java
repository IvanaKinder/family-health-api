package com.familyhealth.api.service;

import com.familyhealth.api.exception.ResourceNotFoundException;
import com.familyhealth.api.generated.model.ChildRequest;
import com.familyhealth.api.generated.model.ChildResponse;
import com.familyhealth.api.mapper.ChildMapper;
import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.User;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final ChildMapper childMapper;
    private final UserRepository userRepository;

    public List<ChildResponse> listChildren() {
        return childRepository.findAllByUser(getCurrentUser()).stream()
                .map(childMapper::toResponse)
                .toList();
    }

    public ChildResponse getChild(Long id) {
        return childMapper.toResponse(findOwnedChild(id));
    }

    public ChildResponse createChild(ChildRequest request) {
        Child child = childMapper.toEntity(request);
        child.setUser(getCurrentUser());
        return childMapper.toResponse(childRepository.save(child));
    }

    public ChildResponse updateChild(Long id, ChildRequest request) {
        Child child = findOwnedChild(id);
        childMapper.updateEntity(request, child);
        return childMapper.toResponse(childRepository.save(child));
    }

    public void deleteChild(Long id) {
        childRepository.delete(findOwnedChild(id));
    }

    private Child findOwnedChild(Long id) {
        return childRepository.findByIdAndUser(id, getCurrentUser())
                .orElseThrow(() -> new ResourceNotFoundException("Child", id));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
