package com.familyhealth.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.User;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.MilestoneRepository;
import com.familyhealth.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "user@example.com")
class MilestonesControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ChildRepository childRepository;
    @Autowired private MilestoneRepository milestoneRepository;

    private long childId;

    @BeforeEach
    void setUp() {
        milestoneRepository.deleteAll();
        childRepository.deleteAll();
        userRepository.deleteAll();
        User user = userRepository.save(User.builder()
                .email("user@example.com").password("test").firstName("Test").lastName("User").build());
        childId = childRepository.save(Child.builder()
                .user(user).firstName("Emma").lastName("Doe").dateOfBirth(LocalDate.of(2020, 3, 10)).build()).getId();
    }

    @Test
    void listMilestones_returnsEmptyPage() throws Exception {
        mockMvc.perform(get(milestonesUrl(childId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void createMilestone_returns201() throws Exception {
        mockMvc.perform(post(milestonesUrl(childId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMilestonePayload())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("First steps"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void updateMilestone_returns200() throws Exception {
        long milestoneId = createMilestone();

        mockMvc.perform(put(milestonesUrl(childId) + "/" + milestoneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "First words", "date", "2021-09-01"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("First words"));
    }

    @Test
    void deleteMilestone_returns204() throws Exception {
        long milestoneId = createMilestone();

        mockMvc.perform(delete(milestonesUrl(childId) + "/" + milestoneId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "other@example.com")
    void milestonesOnOtherUsersChild_returns404() throws Exception {
        mockMvc.perform(get(milestonesUrl(childId)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void withoutToken_returns401() throws Exception {
        mockMvc.perform(get(milestonesUrl(childId)))
                .andExpect(status().isUnauthorized());
    }

    // --- helpers ---

    private String milestonesUrl(long cId) {
        return "/api/v1/children/" + cId + "/milestones";
    }

    private Map<String, String> validMilestonePayload() {
        return Map.of("title", "First steps", "date", "2021-06-01");
    }

    private long createMilestone() throws Exception {
        MvcResult result = mockMvc.perform(post(milestonesUrl(childId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMilestonePayload())))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

}
