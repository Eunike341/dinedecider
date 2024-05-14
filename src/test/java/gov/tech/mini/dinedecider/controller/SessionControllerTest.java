package gov.tech.mini.dinedecider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.tech.mini.dinedecider.domain.SessionDto;
import gov.tech.mini.dinedecider.domain.SubmissionDto;
import gov.tech.mini.dinedecider.domain.UserDto;
import gov.tech.mini.dinedecider.domain.exception.ApiException;
import gov.tech.mini.dinedecider.domain.exception.ErrorCode;
import gov.tech.mini.dinedecider.repo.Session;
import gov.tech.mini.dinedecider.repo.SessionStatus;
import gov.tech.mini.dinedecider.repo.User;
import gov.tech.mini.dinedecider.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SessionController.class)
public class SessionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService sessionService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID sessionUuid;
    private UUID adminUuid;
    private UUID userUuid;
    private SessionDto sessionDto;
    private User adminUser;
    private User user;

    @BeforeEach
    public void setup() {

        adminUuid = UUID.randomUUID();
        userUuid = UUID.randomUUID();
        sessionUuid = UUID.randomUUID();

        adminUser = new User(adminUuid, "Admin", LocalDateTime.now());
        user = new User(userUuid, "User", LocalDateTime.now());
        sessionDto = new SessionDto(null, new UserDto(adminUser), "Test Session", new ArrayList<>());
    }

    @Test
    public void testStartSessionSuccess() throws Exception {
        when(sessionService.startSession(any(SessionDto.class))).thenReturn(sessionDto);

        // Act & Assert
        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionName").value(sessionDto.sessionName()));
    }

    @Test
    public void testEndSessionSessionNotFound() throws Exception {
        when(sessionService.endSession(any(UUID.class), any(UUID.class)))
                .thenThrow(new ApiException("Session not found", ErrorCode.SESSION_NOT_FOUND));

        mockMvc.perform(patch("/sessions/{sessionUuid}", sessionUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminUuid)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Session not found"));
    }

    @Test
    public void testEndSession() throws Exception {
        SubmissionDto submissionDto = new SubmissionDto("Place 1", new UserDto(user), true);
        when(sessionService.endSession(any(UUID.class), any(UUID.class))).thenReturn(submissionDto);

        mockMvc.perform(patch("/sessions/{sessionUuid}", sessionUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminUuid)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selected").exists());
    }

    @Test
    public void testInviteUser() throws Exception {
        List<UserDto> invitees = List.of(new UserDto(user));

        mockMvc.perform(post("/sessions/{sessionUuid}/invitations", sessionUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invitees)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testJoinSession_Success() throws Exception {
        mockMvc.perform(post("/sessions/{sessionUuid}/participants", sessionUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUuid)))
                .andExpect(status().isNoContent());
    }
}
