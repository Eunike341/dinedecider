package gov.tech.mini.dinedecider.service;

import gov.tech.mini.dinedecider.domain.SessionDto;
import gov.tech.mini.dinedecider.domain.SubmissionDto;
import gov.tech.mini.dinedecider.domain.UserDto;
import gov.tech.mini.dinedecider.domain.exception.ApiException;
import gov.tech.mini.dinedecider.domain.exception.ErrorCode;
import gov.tech.mini.dinedecider.repo.*;
import gov.tech.mini.dinedecider.service.decider.PlaceDecider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SessionServiceTest {
    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionUserRepository sessionUserRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private PlaceDecider placeDecider;

    @InjectMocks
    private SessionService sessionService;

    private UUID sessionUuid;
    private UUID adminUuid;
    private UUID userUuid;
    private Session session;
    private User adminUser;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        adminUuid = UUID.randomUUID();
        userUuid = UUID.randomUUID();
        sessionUuid = UUID.randomUUID();
        adminUser = new User(adminUuid, "Admin", LocalDateTime.now());
        user = new User(userUuid, "User", LocalDateTime.now());
        session = new Session(sessionUuid, "Test Session", SessionStatus.ACTIVE, adminUser, LocalDateTime.now());
    }

    @Test
    public void testStartSessionWithoutInvitee() {
        var sessionDto = new SessionDto(null, new UserDto(adminUser), "Test Session", new ArrayList<>());

        when(userRepository.findByUuidIn(any())).thenReturn(Arrays.asList(adminUser));
        when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = sessionService.startSession(sessionDto);

        assertNotNull(result);
        assertEquals("Test Session", result.sessionName());
        verify(sessionRepository).save(any(Session.class));
        verify(userRepository, never()).saveAll(any(List.class));
    }

    @Test
    public void testStartSessionWithInvitee() {
        var sessionDto = new SessionDto(null, new UserDto(adminUser), "Test Session",
                List.of(new UserDto(user)));

        when(userRepository.findByUuidIn(any())).thenReturn(Arrays.asList(adminUser));
        when(userRepository.saveAll(any(List.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = sessionService.startSession(sessionDto);

        assertNotNull(result);
        assertEquals("Test Session", result.sessionName());
        verify(sessionRepository).save(any(Session.class));
        verify(userRepository).saveAll(any(List.class));
    }

    @Test
    public void testEndSessionAdminUserNotFound() {
        when(userRepository.findByUuid(adminUuid)).thenReturn(Optional.empty());

        var thrown = assertThrows(ApiException.class, () -> sessionService.endSession(sessionUuid, adminUuid));
        assertEquals(ErrorCode.USER_NOT_FOUND, thrown.getErrorCode());
    }

    @Test
    public void testEndSessionSessionNotFound() {
        when(userRepository.findByUuid(adminUuid)).thenReturn(Optional.of(adminUser));
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.empty());

        var thrown = assertThrows(ApiException.class, () -> sessionService.endSession(sessionUuid, adminUuid));
        assertEquals(ErrorCode.SESSION_NOT_FOUND, thrown.getErrorCode());
    }

    @Test
    public void testEndSessionHasEnded() {
        when(userRepository.findByUuid(adminUuid)).thenReturn(Optional.of(adminUser));
        session.setEndDatetime(LocalDateTime.now());
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.of(session));

        var thrown = assertThrows(ApiException.class, () -> sessionService.endSession(sessionUuid, adminUuid));
        assertEquals(ErrorCode.SESSION_ENDED, thrown.getErrorCode());
    }

    @Test
    public void testEndSessionWithoutSubmission() {
        when(userRepository.findByUuid(adminUuid)).thenReturn(Optional.of(adminUser));
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.of(session));
        when(submissionRepository.findBySessionUser_Session_Uuid(sessionUuid)).thenReturn(Optional.empty());

        var thrown = assertThrows(ApiException.class, () -> sessionService.endSession(sessionUuid, adminUuid));
        assertEquals(ErrorCode.NO_AVAILABLE_SELECTION, thrown.getErrorCode());
    }

    @Test
    public void testEndSession() {
        when(userRepository.findByUuid(adminUuid)).thenReturn(Optional.of(adminUser));
        when(sessionRepository.findByUuid(sessionUuid)).thenReturn(Optional.of(session));

        var submission = new Submission();
        submission.setSessionUser(new SessionUser());
        submission.getSessionUser().setAttendee(user);
        when(submissionRepository.findBySessionUser_Session_Uuid(sessionUuid))
                .thenReturn(Optional.of(List.of(submission)));
        when(placeDecider.select(any())).thenAnswer(invocation -> submission);
        when(submissionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SubmissionDto result = sessionService.endSession(sessionUuid, adminUuid);
        assertNotNull(result);
        assertEquals(true, result.selected());
    }

    @Test
    public void testJoinInvalidUserSession() {
        when(sessionUserRepository
                .findByStatusAndAttendee_UuidAndSession_UuidAndSession_Status(MemberStatus.INVITED, userUuid, sessionUuid, SessionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        var thrown = assertThrows(ApiException.class, () -> sessionService.joinSession(sessionUuid, userUuid));
        assertEquals(ErrorCode.INVALID_JOIN_ATTEMPT, thrown.getErrorCode());
    }

    @Test
    public void testJoinSession() {
        var sessionUser = new SessionUser(user, session, MemberStatus.INVITED);

        when(sessionUserRepository
                .findByStatusAndAttendee_UuidAndSession_UuidAndSession_Status(MemberStatus.INVITED, userUuid, sessionUuid, SessionStatus.ACTIVE))
                .thenReturn(Optional.of(sessionUser));

        sessionService.joinSession(sessionUuid, userUuid);

        assertEquals(MemberStatus.JOINED, sessionUser.getStatus());
        verify(sessionUserRepository).save(sessionUser);
    }

    @Test
    public void testInviteUsersSessionNotFound() {
        when(sessionRepository.findByUuidAndStatus(sessionUuid, SessionStatus.ACTIVE)).thenReturn(Optional.empty());

        var thrown = assertThrows(ApiException.class, () -> sessionService.inviteUsers(sessionUuid, List.of(new UserDto(user))));
        assertEquals(ErrorCode.SESSION_NOT_FOUND, thrown.getErrorCode());
    }

    @Test
    public void testInviteUsers() {
        when(sessionRepository.findByUuidAndStatus(sessionUuid, SessionStatus.ACTIVE)).thenReturn(Optional.of(session));
        when(userRepository.findByUuidIn(any())).thenReturn(Arrays.asList(user));

        sessionService.inviteUsers(sessionUuid, List.of(new UserDto(user)));
        verify(sessionUserRepository).saveAll(anyList());
    }


}
