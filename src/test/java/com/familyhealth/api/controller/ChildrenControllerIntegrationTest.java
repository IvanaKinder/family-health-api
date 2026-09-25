package com.familyhealth.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.User;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.MilestoneRepository;
import com.familyhealth.api.repository.ReminderRepository;
import com.familyhealth.api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "user@example.com")
class ChildrenControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ChildRepository childRepository;
    @Autowired private MilestoneRepository milestoneRepository;
    @Autowired private ReminderRepository reminderRepository;

    private static final String CHILDREN_URL = "/api/v1/children";
    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("user@example.com").password("test").firstName("Test").lastName("User").build());
    }

    @AfterEach
    void tearDown() {
        reminderRepository.deleteAll();
        milestoneRepository.deleteAll();
        childRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void listChildren_authenticated_returnsEmptyPage() throws Exception {
        mockMvc.perform(get(CHILDREN_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void createChild_withValidPayload_returns201() throws Exception {
        mockMvc.perform(post(CHILDREN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validChildPayload())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Emma"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.id").isNumber());

        assertThat(reminderRepository.findAll())
                .hasSize(1)
                .first()
                .satisfies(r -> {
                    assertThat(r.getTitle()).contains("Emma");
                    assertThat(r.getDueDate()).isEqualTo(LocalDate.of(2021, 3, 10));
                });
    }

    @Test
    void getChild_existingId_returns200() throws Exception {
        long id = createChild();

        mockMvc.perform(get(CHILDREN_URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void getChild_nonExistingId_returns404() throws Exception {
        mockMvc.perform(get(CHILDREN_URL + "/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateChild_withValidPayload_returns200() throws Exception {
        long id = createChild();

        mockMvc.perform(put(CHILDREN_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "firstName", "Emily", "lastName", "Doe", "dateOfBirth", "2020-05-15"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Emily"));
    }

    @Test
    void deleteChild_existingId_returns204() throws Exception {
        long id = createChild();

        mockMvc.perform(delete(CHILDREN_URL + "/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "other@example.com")
    void getChild_otherUsersChild_returns404() throws Exception {
        // Create child owned by user@example.com directly via repository
        Child child = childRepository.save(Child.builder()
                .user(user).firstName("Emma").lastName("Doe")
                .dateOfBirth(LocalDate.of(2020, 3, 10)).build());

        mockMvc.perform(get(CHILDREN_URL + "/" + child.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void listChildren_withoutToken_returns401() throws Exception {
        mockMvc.perform(get(CHILDREN_URL))
                .andExpect(status().isUnauthorized());
    }

    // --- helpers ---

    private Map<String, String> validChildPayload() {
        return Map.of("firstName", "Emma", "lastName", "Doe", "dateOfBirth", "2020-03-10");
    }

    private long createChild() throws Exception {
        MvcResult result = mockMvc.perform(post(CHILDREN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validChildPayload())))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
