package gov.tech.mini.dinedecider.service;

import gov.tech.mini.dinedecider.domain.SubmissionRequestDto;
import gov.tech.mini.dinedecider.domain.UserUuidDto;
import gov.tech.mini.dinedecider.domain.exception.ApiException;
import gov.tech.mini.dinedecider.domain.exception.ErrorCode;
import gov.tech.mini.dinedecider.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private SessionUserRepository sessionUserRepository;

    @InjectMocks
    private SubmissionService submissionService;

    private UUID sessionUuid;
    private User user;
    private Submission submission;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sessionUuid = UUID.randomUUID();
        user = new User(UUID.randomUUID(), "User", LocalDateTime.now());
        submission = new Submission("Place");
        submission.setSessionUser(new SessionUser());
        submission.getSessionUser().setAttendee(user);
    }

    @Test
    public void testGetSubmittedPlacesEmptySubmission () {
        when(sessionUserRepository.findByStatusAndAttendee_UuidAndSession_Uuid(MemberStatus.JOINED, user.getUuid(), sessionUuid))
                .thenReturn(Optional.of(submission.getSessionUser()));
        when(submissionRepository.findBySessionUser_Session_Uuid(sessionUuid)).thenReturn(Optional.empty());
        var result = submissionService.getSubmittedPlaces(sessionUuid, user.getUuid());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetSubmittedPlaces () {
        when(sessionUserRepository.findByStatusAndAttendee_UuidAndSession_Uuid(MemberStatus.JOINED, user.getUuid(), sessionUuid))
                .thenReturn(Optional.of(submission.getSessionUser()));
        List<Submission> submissions = List.of(submission);
        when(submissionRepository.findBySessionUser_Session_Uuid(sessionUuid)).thenReturn(Optional.of(submissions));
        var result = submissionService.getSubmittedPlaces(sessionUuid, user.getUuid());
        assertEquals(1, result.size());
    }

    @Test
    public void testGetSelectedPlaceEmptySubmission () {
        when(sessionUserRepository.findByStatusAndAttendee_UuidAndSession_Uuid(MemberStatus.JOINED, user.getUuid(), sessionUuid))
                .thenReturn(Optional.of(submission.getSessionUser()));
        when(submissionRepository.findBySelectedAndSessionUser_Session_Uuid(true, sessionUuid)).thenReturn(Optional.empty());
        var result = submissionService.getSelectedPlace(sessionUuid, user.getUuid());
        assertNull(result);
    }

    @Test
    public void testGetSelectedPlace () {
        when(sessionUserRepository.findByStatusAndAttendee_UuidAndSession_Uuid(MemberStatus.JOINED, user.getUuid(), sessionUuid))
                .thenReturn(Optional.of(submission.getSessionUser()));
        when(submissionRepository.findBySelectedAndSessionUser_Session_Uuid(true, sessionUuid)).thenReturn(Optional.of(submission));
        var result = submissionService.getSelectedPlace(sessionUuid, user.getUuid());
        assertEquals("Place", result.placeName());
    }

    @Test
    public void testSubmitPlaceInvalidSession () {
        when(sessionUserRepository.findByStatusAndAttendee_UuidAndSession_UuidAndSession_Status(MemberStatus.JOINED, user.getUuid(), sessionUuid, SessionStatus.ACTIVE))
                .thenReturn(Optional.empty());
        var thrown = assertThrows(ApiException.class, () ->
                submissionService.submitPlace(sessionUuid, new SubmissionRequestDto(submission.getPlaceName(), new UserUuidDto(user.getUuid()))));
        assertEquals(ErrorCode.INVALID_SUBMISSION, thrown.getErrorCode());
    }

    @Test
    public void testSubmitPlace () {
        when(sessionUserRepository.findByStatusAndAttendee_UuidAndSession_UuidAndSession_Status(MemberStatus.JOINED, user.getUuid(), sessionUuid, SessionStatus.ACTIVE))
                .thenReturn(Optional.of(submission.getSessionUser()));
        when(submissionRepository.save(any(Submission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        submissionService.submitPlace(sessionUuid, new SubmissionRequestDto(submission.getPlaceName(), new UserUuidDto(user.getUuid())));
        verify(submissionRepository).save(any(Submission.class));
    }
}
