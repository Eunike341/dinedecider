package gov.tech.mini.dinedecider.service;

import gov.tech.mini.dinedecider.domain.SessionDto;
import gov.tech.mini.dinedecider.domain.SessionStartRequestDto;
import gov.tech.mini.dinedecider.domain.SubmissionDto;
import gov.tech.mini.dinedecider.domain.UserDto;
import gov.tech.mini.dinedecider.domain.exception.ApiException;
import gov.tech.mini.dinedecider.domain.exception.ErrorCode;
import gov.tech.mini.dinedecider.repo.*;
import gov.tech.mini.dinedecider.service.decider.PlaceDecider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SessionService {
    private static final Logger LOG = LoggerFactory.getLogger(SessionService.class);
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SessionUserRepository sessionUserRepository;
    private final SubmissionRepository submissionRepository;
    private final PlaceDecider placeDecider;

    public SessionService(SessionRepository sessionRepository, UserRepository userRepository,
                          SessionUserRepository sessionUserRepository, SubmissionRepository submissionRepository, PlaceDecider placeDecider) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.sessionUserRepository = sessionUserRepository;
        this.submissionRepository = submissionRepository;
        this.placeDecider = placeDecider;
    }

    @Transactional
    public SessionDto startSession(SessionStartRequestDto sessionDto) {
        var adminAndInvitees = Stream.concat(Stream.of(sessionDto.admin()),
                        Optional.ofNullable(sessionDto.invitees()).map(List::stream).orElseGet(Stream::empty))
                .collect(Collectors.toList());
        var userUuidMap = loadUsers(adminAndInvitees);

        var adminUser = userUuidMap.get(sessionDto.admin().userUuid());
        var session = this.sessionRepository.save(
                new Session(UUID.randomUUID(), sessionDto.sessionName(), SessionStatus.ACTIVE, adminUser, LocalDateTime.now())
        );
        LOG.debug("New session started: {}", session);
        sessionUserRepository.save(new SessionUser(adminUser, session, MemberStatus.JOINED));

        var inviteesDto = new ArrayList<UserDto>();
        if (sessionDto.invitees()!=null && !sessionDto.invitees().isEmpty()) {
            var sessionInvitees = sessionDto.invitees().stream()
                    .map(invitee -> new SessionUser(userUuidMap.get(invitee.userUuid()), session, MemberStatus.INVITED))
                    .collect(Collectors.toList());
            var newSessionInvitees = sessionUserRepository.saveAll(sessionInvitees);
            inviteesDto.addAll(newSessionInvitees.stream().map(s -> new UserDto(s.getAttendee())).collect(Collectors.toList()));
            LOG.debug("Invitees added for session: {}", session);
        }

        return new SessionDto(session.getUuid(), new UserDto(adminUser), session.getName(), inviteesDto);
    }

    // To reduce DB calls to user table, especially when request contains a lot of invitees, let's load all the users first.
    private Map<UUID, User> loadUsers ( List<UserDto> inviteesDto) {
        var userUuids = new ArrayList<UUID>();
        if(inviteesDto!=null) {
            userUuids.addAll(inviteesDto.stream().map(UserDto::userUuid).collect(Collectors.toList()));
        }

        var users = userRepository.findByUuidIn(userUuids);
        var userUuidMap = users.stream().collect(Collectors.toMap(User::getUuid, Function.identity()));

        //As user management (login and registration) might be from another service, we'll insert all the uuids that do not exist.
        insertNewUsers(userUuidMap, inviteesDto);

        return userUuidMap;
    }

    //If userUuid does not exist in DB, it will be created as new record.
    private void insertNewUsers (Map<UUID, User> userUuidMap, List<UserDto> inviteesDto) {
        var newUsersDto = new ArrayList<UserDto>();
        if(inviteesDto!=null) {
            newUsersDto.addAll(inviteesDto.stream()
                    .filter(userDto -> !userUuidMap.containsKey(userDto.userUuid()))
                    .collect(Collectors.toList()));
        }

        if (!newUsersDto.isEmpty()) {
            var newUsers = userRepository.saveAll(newUsersDto.stream()
                    .map(dto -> new User(dto.userUuid(), dto.name(), LocalDateTime.now()))
                    .collect(Collectors.toList()));
            userUuidMap.putAll(newUsers.stream().collect(Collectors.toMap(User::getUuid, Function.identity())));
            LOG.debug("Added new users: {}", newUsers);
        }
    }

    @Transactional
    public SubmissionDto endSession(UUID sessionUuid, UUID adminUserUuid) {
        var adminUser = userRepository.findByUuid(adminUserUuid)
                .orElseThrow(() -> new ApiException("User not found", ErrorCode.USER_NOT_FOUND));
        var session = sessionRepository.findByUuid(sessionUuid)
                .orElseThrow(() -> new ApiException("Session not found", ErrorCode.SESSION_NOT_FOUND));

        if (!session.getAdmin().equals(adminUser)) {
            throw new ApiException("User is not admin", ErrorCode.USER_NOT_ADMIN);
        }

        if (session.getEndDatetime()!=null) {
            throw new ApiException("Session has ended", ErrorCode.SESSION_ENDED);
        }

        var submissions = submissionRepository.findBySessionUser_Session_Uuid(sessionUuid);
        if (submissions.isEmpty() || submissions.get().isEmpty()) {
            throw new ApiException("There is no place submitted yet", ErrorCode.NO_AVAILABLE_SELECTION);
        }
        var selectedSubmission = placeDecider.select(submissions.get());
        selectedSubmission.setSelected(true);
        submissionRepository.save(selectedSubmission);
        LOG.debug("Selected submission: {}", selectedSubmission);

        session.setStatus(SessionStatus.ENDED);
        session.setEndDatetime(LocalDateTime.now());
        sessionRepository.save(session);
        LOG.debug("Session has ended: {}", session);
        return new SubmissionDto(selectedSubmission.getPlaceName(),
                new UserDto(selectedSubmission.getSessionUser().getAttendee()),
                selectedSubmission.isSelected());
    }

    public void joinSession(UUID sessionUuid, UUID userUuid) {
        var sessionUser = sessionUserRepository.findByStatusAndAttendee_UuidAndSession_UuidAndSession_Status(
                MemberStatus.INVITED, userUuid, sessionUuid, SessionStatus.ACTIVE)
                .orElseThrow(() -> new ApiException("Invalid attempt to join", ErrorCode.INVALID_JOIN_ATTEMPT));
        sessionUser.setStatus(MemberStatus.JOINED);
        sessionUserRepository.save(sessionUser);
        LOG.debug("User joined a session: {}", sessionUser);
    }

    @Transactional
    public void inviteUsers(UUID sessionUuid, List<UserDto> users) {
        var session = this.sessionRepository.findByUuidAndStatus(sessionUuid, SessionStatus.ACTIVE)
                .orElseThrow(()->new ApiException("Session not found", ErrorCode.SESSION_NOT_FOUND));

        var userUuidMap = loadUsers(users);

        var sessionInvitees = users.stream()
                .map(invitee -> new SessionUser(userUuidMap.get(invitee.userUuid()), session, MemberStatus.INVITED))
                .collect(Collectors.toList());
        sessionUserRepository.saveAll(sessionInvitees);
        LOG.debug("More users invited to session: {}", sessionInvitees);
    }
}
