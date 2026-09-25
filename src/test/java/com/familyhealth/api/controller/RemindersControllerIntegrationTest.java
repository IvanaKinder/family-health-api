package com.familyhealth.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.familyhealth.api.model.Child;
import com.familyhealth.api.model.User;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.ReminderRepository;
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
class RemindersControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ChildRepository childRepository;
    @Autowired private ReminderRepository reminderRepository;

    private long childId;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        childRepository.deleteAll();
        userRepository.deleteAll();
        User user = userRepository.save(User.builder()
                .email("user@example.com").password("test").firstName("Test").lastName("User").build());
        childId = childRepository.save(Child.builder()
                .user(user).firstName("Emma").lastName("Doe").dateOfBirth(LocalDate.of(2020, 3, 10)).build()).getId();
    }

    @Test
    void listReminders_returnsEmptyPage() throws Exception {
        mockMvc.perform(get(remindersUrl(childId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void createReminder_returns201() throws Exception {
        mockMvc.perform(post(remindersUrl(childId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validReminderPayload())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Dentist"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void updateReminder_returns200() throws Exception {
        long reminderId = createReminder();

        mockMvc.perform(put(remindersUrl(childId) + "/" + reminderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Eye doctor", "dueDate", "2024-04-01"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Eye doctor"));
    }

    @Test
    void deleteReminder_returns204() throws Exception {
        long reminderId = createReminder();

        mockMvc.perform(delete(remindersUrl(childId) + "/" + reminderId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "other@example.com")
    void remindersOnOtherUsersChild_returns404() throws Exception {
        mockMvc.perform(get(remindersUrl(childId)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void withoutToken_returns401() throws Exception {
        mockMvc.perform(get(remindersUrl(childId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listReminders_withFromDate_returnsOnlyRemindersOnOrAfter() throws Exception {
        createReminderWithDate("2024-02-01");
        createReminderWithDate("2024-03-15");
        createReminderWithDate("2024-04-01");

        mockMvc.perform(get(remindersUrl(childId)).param("fromDate", "2024-03-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void listReminders_withToDate_returnsOnlyRemindersOnOrBefore() throws Exception {
        createReminderWithDate("2024-02-01");
        createReminderWithDate("2024-03-15");
        createReminderWithDate("2024-04-01");

        mockMvc.perform(get(remindersUrl(childId)).param("toDate", "2024-03-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void listReminders_withBothDates_returnsOnlyRemindersInRange() throws Exception {
        createReminderWithDate("2024-02-01");
        createReminderWithDate("2024-03-15");
        createReminderWithDate("2024-04-01");

        mockMvc.perform(get(remindersUrl(childId))
                        .param("fromDate", "2024-03-01")
                        .param("toDate", "2024-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].dueDate").value("2024-03-15"));
    }

    // --- helpers ---

    private String remindersUrl(long cId) {
        return "/api/v1/children/" + cId + "/reminders";
    }

    private Map<String, String> validReminderPayload() {
        return Map.of("title", "Dentist", "dueDate", "2024-03-15");
    }

    private long createReminder() throws Exception {
        MvcResult result = mockMvc.perform(post(remindersUrl(childId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validReminderPayload())))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private void createReminderWithDate(String dueDate) throws Exception {
        mockMvc.perform(post(remindersUrl(childId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Reminder", "dueDate", dueDate))))
                .andExpect(status().isCreated());
    }
}
