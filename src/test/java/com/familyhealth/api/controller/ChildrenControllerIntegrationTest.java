package com.familyhealth.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.familyhealth.api.repository.ChildRepository;
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
class ChildrenControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ChildRepository childRepository;

    private static final String CHILDREN_URL = "/api/v1/children";
    private String jwt;

    @BeforeEach
    void setUp() throws Exception {
        childRepository.deleteAll();
        userRepository.deleteAll();
        jwt = registerAndLogin("user@example.com", "password123");
    }

    @Test
    void listChildren_authenticated_returnsEmptyList() throws Exception {
        mockMvc.perform(get(CHILDREN_URL).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createChild_withValidPayload_returns201() throws Exception {
        mockMvc.perform(post(CHILDREN_URL)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validChildPayload())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Emma"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void getChild_existingId_returns200() throws Exception {
        long id = createChild();

        mockMvc.perform(get(CHILDREN_URL + "/" + id).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void getChild_nonExistingId_returns404() throws Exception {
        mockMvc.perform(get(CHILDREN_URL + "/999").header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateChild_withValidPayload_returns200() throws Exception {
        long id = createChild();

        Map<String, String> updated = Map.of(
                "firstName", "Emily",
                "lastName", "Doe",
                "dateOfBirth", "2020-05-15"
        );

        mockMvc.perform(put(CHILDREN_URL + "/" + id)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Emily"));
    }

    @Test
    void deleteChild_existingId_returns204() throws Exception {
        long id = createChild();

        mockMvc.perform(delete(CHILDREN_URL + "/" + id).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNoContent());
    }

    @Test
    void getChild_otherUsersChild_returns404() throws Exception {
        long id = createChild();

        String otherJwt = registerAndLogin("other@example.com", "password123");

        mockMvc.perform(get(CHILDREN_URL + "/" + id).header("Authorization", "Bearer " + otherJwt))
                .andExpect(status().isNotFound());
    }

    @Test
    void listChildren_withoutToken_returns401() throws Exception {
        mockMvc.perform(get(CHILDREN_URL))
                .andExpect(status().isUnauthorized());
    }

    // --- helpers ---

    private Map<String, String> validChildPayload() {
        return Map.of(
                "firstName", "Emma",
                "lastName", "Doe",
                "dateOfBirth", "2020-03-10"
        );
    }

    private long createChild() throws Exception {
        MvcResult result = mockMvc.perform(post(CHILDREN_URL)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validChildPayload())))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    private String registerAndLogin(String email, String password) throws Exception {
        Map<String, String> registerPayload = Map.of(
                "email", email,
                "password", password,
                "firstName", "Test",
                "lastName", "User"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload)))
                .andExpect(status().isCreated());

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("token").asText();
    }
}
