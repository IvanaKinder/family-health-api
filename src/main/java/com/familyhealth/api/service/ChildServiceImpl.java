package com.familyhealth.api.service;

import com.familyhealth.api.exception.ResourceNotFoundException;
import com.familyhealth.api.mapper.ChildMapper;
import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.User;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.UserRepository;
import com.familyhealth.api.service.model.ChildCommand;
import com.familyhealth.api.service.model.ChildView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChildServiceImpl implements ChildService {

    private final ChildRepository childRepository;
    private final ChildMapper childMapper;
    private final UserRepository userRepository;

    @Override
    public Page<ChildView> listChildren(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));
        return childRepository.findAllByUser(user, pageable).map(childMapper::toView);
    }

    @Override
    public ChildView getChild(Long id) {
        return childMapper.toView(childRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Child", id)));
    }

    @Override
    @Transactional
    public ChildView createChild(ChildCommand command) {
        User user = userRepository.findByEmail(command.userEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + command.userEmail()));
        log.info("Creating child '{}' for user: {}", command.firstName(), user.getEmail());
        Child child = childMapper.toEntity(command);
        child.setUser(user);
        ChildView view = childMapper.toView(childRepository.save(child));
        log.debug("Child created with id: {}", view.id());
        return view;
    }

    @Override
    @Transactional
    public ChildView updateChild(Long id, ChildCommand command) {
        log.info("Updating child id: {}", id);
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Child", id));
        childMapper.updateEntity(command, child);
        return childMapper.toView(childRepository.save(child));
    }

    @Override
    @Transactional
    public void deleteChild(Long id) {
        log.info("Deleting child id: {}", id);
        childRepository.delete(childRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Child", id)));
    }
}
