package com.familyhealth.api.service;

import com.familyhealth.api.exception.ResourceNotFoundException;
import com.familyhealth.api.generated.model.MilestoneRequest;
import com.familyhealth.api.generated.model.MilestoneResponse;
import com.familyhealth.api.mapper.MilestoneMapper;
import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.Milestone;
import com.familyhealth.api.model.User;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.MilestoneRepository;
import com.familyhealth.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MilestoneServiceTest {

    @Mock private MilestoneRepository milestoneRepository;
    @Mock private ChildRepository childRepository;
    @Mock private UserRepository userRepository;

    private final MilestoneMapper milestoneMapper = Mappers.getMapper(MilestoneMapper.class);
    private MilestoneService milestoneService;

    private User user;
    private Child child;
    private Milestone milestone;
    private MilestoneRequest milestoneRequest;

    @BeforeEach
    void setUp() {
        milestoneService = new MilestoneService(milestoneRepository, milestoneMapper, childRepository, userRepository);

        user = User.builder().id(1L).email("user@example.com").build();
        child = Child.builder().id(10L).user(user).firstName("Emma").build();
        milestone = Milestone.builder().id(20L).child(child).title("First steps").date(LocalDate.of(2021, 6, 1)).build();
        milestoneRequest = new MilestoneRequest().title("First steps").date(LocalDate.of(2021, 6, 1));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user@example.com", null, List.of())
        );
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
    }

    @Test
    void listMilestones_returnsMilestonesForOwnedChild() {
        when(childRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(child));
        when(milestoneRepository.findAllByChild(child)).thenReturn(List.of(milestone));

        List<MilestoneResponse> result = milestoneService.listMilestones(10L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("First steps");
    }

    @Test
    void listMilestones_childNotOwned_throwsResourceNotFoundException() {
        when(childRepository.findByIdAndUser(10L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> milestoneService.listMilestones(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("10");
    }

    @Test
    void createMilestone_savesWithChildAndReturnsResponse() {
        when(childRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(child));
        when(milestoneRepository.save(any(Milestone.class))).thenAnswer(inv -> {
            Milestone saved = inv.getArgument(0);
            saved.setId(20L);
            return saved;
        });

        MilestoneResponse result = milestoneService.createMilestone(10L, milestoneRequest);

        assertThat(result.getTitle()).isEqualTo("First steps");
        verify(milestoneRepository).save(any(Milestone.class));
    }

    @Test
    void updateMilestone_existingId_updatesFields() {
        when(childRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(child));
        when(milestoneRepository.findByIdAndChild(20L, child)).thenReturn(Optional.of(milestone));
        when(milestoneRepository.save(milestone)).thenReturn(milestone);

        MilestoneRequest updateRequest = new MilestoneRequest().title("First words").date(LocalDate.of(2021, 9, 1));
        MilestoneResponse result = milestoneService.updateMilestone(10L, 20L, updateRequest);

        assertThat(result.getTitle()).isEqualTo("First words");
        verify(milestoneRepository).save(milestone);
    }

    @Test
    void updateMilestone_milestoneNotFound_throwsResourceNotFoundException() {
        when(childRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(child));
        when(milestoneRepository.findByIdAndChild(999L, child)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> milestoneService.updateMilestone(10L, 999L, milestoneRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void deleteMilestone_existingId_deletes() {
        when(childRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(child));
        when(milestoneRepository.findByIdAndChild(20L, child)).thenReturn(Optional.of(milestone));

        milestoneService.deleteMilestone(10L, 20L);

        verify(milestoneRepository).delete(milestone);
    }

    @Test
    void deleteMilestone_childNotOwned_throwsResourceNotFoundException() {
        when(childRepository.findByIdAndUser(10L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> milestoneService.deleteMilestone(10L, 20L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("10");
    }
}
