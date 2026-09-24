package com.familyhealth.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.familyhealth.api.repository.ChildRepository;
import com.familyhealth.api.repository.ReminderRepository;
import com.familyhealth.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RemindersControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ChildRepository childRepository;
    @Autowired private ReminderRepository reminderRepository;

    private String jwt;
    private long childId;

    @BeforeEach
    void setUp() throws Exception {
        reminderRepository.deleteAll();
        childRepository.deleteAll();
        userRepository.deleteAll();
        jwt = registerAndLogin("user@example.com", "password123");
        childId = createChild(jwt);
    }

    @Test
    void listReminders_returnsEmptyList() throws Exception {
        mockMvc.perform(get(remindersUrl(childId)).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createReminder_returns201() throws Exception {
        mockMvc.perform(post(remindersUrl(childId))
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validReminderPayload())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Dentist"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void updateReminder_returns200() throws Exception {
        long reminderId = createReminder(childId, jwt);

        Map<String, String> updated = Map.of("title", "Eye doctor", "dueDate", "2024-04-01");

        mockMvc.perform(put(remindersUrl(childId) + "/" + reminderId)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Eye doctor"));
    }

    @Test
    void deleteReminder_returns204() throws Exception {
        long reminderId = createReminder(childId, jwt);

        mockMvc.perform(delete(remindersUrl(childId) + "/" + reminderId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNoContent());
    }

    @Test
    void remindersOnOtherUsersChild_returns404() throws Exception {
        String otherJwt = registerAndLogin("other@example.com", "password123");

        mockMvc.perform(get(remindersUrl(childId)).header("Authorization", "Bearer " + otherJwt))
                .andExpect(status().isNotFound());
    }

    @Test
    void withoutToken_returns401() throws Exception {
        mockMvc.perform(get(remindersUrl(childId)))
                .andExpect(status().isUnauthorized());
    }

    // --- helpers ---

    private String remindersUrl(long cId) {
        return "/api/v1/children/" + cId + "/reminders";
    }

    private Map<String, String> validReminderPayload() {
        return Map.of("title", "Dentist", "dueDate", "2024-03-15");
    }

    private long createReminder(long cId, String token) throws Exception {
        MvcResult result = mockMvc.perform(post(remindersUrl(cId))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validReminderPayload())))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private long createChild(String token) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/children")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "firstName", "Emma", "lastName", "Doe", "dateOfBirth", "2020-03-10"))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private String registerAndLogin(String email, String password) throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email, "password", password, "firstName", "Test", "lastName", "User"))))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
}
