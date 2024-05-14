package gov.tech.mini.dinedecider.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.tech.mini.dinedecider.domain.SubmissionDto;
import gov.tech.mini.dinedecider.domain.UserDto;
import gov.tech.mini.dinedecider.domain.exception.ApiException;
import gov.tech.mini.dinedecider.domain.exception.ErrorCode;
import gov.tech.mini.dinedecider.service.SubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SubmissionController.class)
public class SubmissionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubmissionService submissionService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID sessionUuid;
    private SubmissionDto submissionDto;
    private List<SubmissionDto> submissionDtoList;

    @BeforeEach
    void setUp() {
        sessionUuid = UUID.randomUUID();
        submissionDto = new SubmissionDto("Place 1", new UserDto(UUID.randomUUID(), "User"), true);
        submissionDtoList = Arrays.asList(submissionDto);
    }

    @Test
    void testSubmitPlaceMissingPlaceName() throws Exception {
        mockMvc.perform(post("/submissions/" + sessionUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSubmitPlaceInvalid() throws Exception {
        doThrow(new ApiException("User not found on session", ErrorCode.INVALID_SUBMISSION))
                .when(submissionService).submitPlace(eq(sessionUuid), any(SubmissionDto.class));


        mockMvc.perform(post("/submissions/" + sessionUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submissionDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errCode").value(ErrorCode.INVALID_SUBMISSION.name()));
    }

    @Test
    void testSubmitPlace() throws Exception {
        doNothing().when(submissionService).submitPlace(eq(sessionUuid), any(SubmissionDto.class));

        mockMvc.perform(post("/submissions/" + sessionUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submissionDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testViewSubmissionNoValue() throws Exception {
        when(submissionService.getSubmittedPlaces(eq(sessionUuid))).thenReturn(List.of());

        mockMvc.perform(get("/submissions/" + sessionUuid))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void testViewSubmission() throws Exception {
        when(submissionService.getSubmittedPlaces(eq(sessionUuid))).thenReturn(submissionDtoList);

        mockMvc.perform(get("/submissions/" + sessionUuid))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{\"placeName\": \"Place 1\"}]"));
    }

    @Test
    void testViewSelectedPlaceNoValue() throws Exception {
        when(submissionService.getSelectedPlace(eq(sessionUuid))).thenReturn(null);

        mockMvc.perform(get("/submissions/" + sessionUuid + "/selected"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void testViewSelectedPlace() throws Exception {
        when(submissionService.getSelectedPlace(eq(sessionUuid))).thenReturn(submissionDto);

        mockMvc.perform(get("/submissions/" + sessionUuid + "/selected"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"placeName\": \"Place 1\"}"));  // Replace with actual JSON body
    }
}
