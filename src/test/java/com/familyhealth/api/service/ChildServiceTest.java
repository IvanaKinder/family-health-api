package com.familyhealth.api.service;

import com.familyhealth.api.exception.ResourceNotFoundException;
import com.familyhealth.api.generated.model.ChildRequest;
import com.familyhealth.api.generated.model.ChildResponse;
import com.familyhealth.api.mapper.ChildMapper;
import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.User;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChildServiceTest {

    @Mock private ChildRepository childRepository;
    @Mock private UserRepository userRepository;

    private final ChildMapper childMapper = Mappers.getMapper(ChildMapper.class);
    private ChildServiceImpl childService;

    private User user;
    private Child child;
    private ChildRequest childRequest;

    @BeforeEach
    void setUp() {
        childService = new ChildServiceImpl(childRepository, childMapper, userRepository);

        user = User.builder().id(1L).email("user@example.com").build();
        child = Child.builder().id(10L).user(user).firstName("Emma").lastName("Doe").build();
        childRequest = new ChildRequest().firstName("Emma").lastName("Doe");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user@example.com", null, List.of())
        );
    }

    @Test
    void listChildren_returnsAllChildrenForCurrentUser() {
        Pageable pageable = PageRequest.of(0, 20);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(childRepository.findAllByUser(eq(user), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(child), pageable, 1));

        var result = childService.listChildren(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getFirstName()).isEqualTo("Emma");
        assertThat(result.getContent().getFirst().getLastName()).isEqualTo("Doe");
        assertThat(result.getTotalElements()).isEqualTo(1L);
        verify(childRepository).findAllByUser(eq(user), any(Pageable.class));
    }

    @Test
    void listChildren_whenNoChildren_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 20);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(childRepository.findAllByUser(eq(user), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(), pageable, 0));

        assertThat(childService.listChildren(pageable).getContent()).isEmpty();
    }

    @Test
    void getChild_existingId_returnsChildResponse() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));

        ChildResponse result = childService.getChild(10L);

        assertThat(result.getFirstName()).isEqualTo("Emma");
        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void getChild_nonExistingId_throwsResourceNotFoundException() {
        when(childRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> childService.getChild(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void createChild_savesEntityWithCurrentUserAndReturnsResponse() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(childRepository.save(any(Child.class))).thenAnswer(inv -> {
            Child saved = inv.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        ChildResponse result = childService.createChild(childRequest);

        assertThat(result.getFirstName()).isEqualTo("Emma");
        assertThat(result.getLastName()).isEqualTo("Doe");
        verify(childRepository).save(argThat(c -> c.getUser().equals(user)));
    }

    @Test
    void updateChild_existingId_updatesFieldsAndReturnsResponse() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));
        when(childRepository.save(child)).thenReturn(child);

        ChildRequest updateRequest = new ChildRequest().firstName("Emily").lastName("Doe");
        ChildResponse result = childService.updateChild(10L, updateRequest);

        assertThat(result.getFirstName()).isEqualTo("Emily");
        verify(childRepository).save(child);
    }

    @Test
    void updateChild_nonExistingId_throwsResourceNotFoundException() {
        when(childRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> childService.updateChild(999L, childRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void deleteChild_existingId_deletesChild() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));

        childService.deleteChild(10L);

        verify(childRepository).delete(child);
    }

    @Test
    void deleteChild_nonExistingId_throwsResourceNotFoundException() {
        when(childRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> childService.deleteChild(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
