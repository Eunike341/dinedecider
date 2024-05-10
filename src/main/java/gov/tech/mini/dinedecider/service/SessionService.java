package gov.tech.mini.dinedecider.service;

import gov.tech.mini.dinedecider.domain.SessionDto;
import gov.tech.mini.dinedecider.domain.UserDto;
import gov.tech.mini.dinedecider.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SessionAttendeeRepository sessionAttendeeRepository;

    public SessionService(SessionRepository sessionRepository, UserRepository userRepository,
                          SessionAttendeeRepository sessionAttendeeRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.sessionAttendeeRepository = sessionAttendeeRepository;
    }

    @Transactional
    public SessionDto startSession(SessionDto sessionDto) {
        var userUuidMap = loadUsers(sessionDto.admin(), sessionDto.invitees());

        var adminUser = userUuidMap.get(sessionDto.admin().userUuid());
        var session = this.sessionRepository.save(
                new Session(UUID.randomUUID(), sessionDto.sessionName(), adminUser.getId(), LocalDateTime.now())
        );

        var inviteesDto = new ArrayList<UserDto>();
        if (sessionDto.invitees()!=null && !sessionDto.invitees().isEmpty()) {
            var sessionInvitees = sessionDto.invitees().stream()
                    .map(invitee -> new SessionInvitee(userUuidMap.get(invitee.userUuid()), session.getId(), MemberStatus.INVITED))
                    .collect(Collectors.toList());
            var newSessionInvitees = sessionAttendeeRepository.saveAll(sessionInvitees);
            inviteesDto.addAll(newSessionInvitees.stream().map(s -> new UserDto(s.getAttendee())).collect(Collectors.toList()));
        }

        return new SessionDto(session.getUuid(), new UserDto(adminUser), session.getName(), inviteesDto);
    }

    // To reduce DB calls to user table, especially when request contains a lot of invitees, let's load all the users first.
    private Map<UUID, User> loadUsers (UserDto adminDto, List<UserDto> inviteesDto) {
        var userUuids = new ArrayList<UUID>();
        userUuids.add(adminDto.userUuid());
        if(inviteesDto!=null) {
            userUuids.addAll(inviteesDto.stream().map(UserDto::userUuid).collect(Collectors.toList()));
        }

        var users = userRepository.findByUuidIn(userUuids);
        var userUuidMap = users.stream().collect(Collectors.toMap(User::getUuid, Function.identity()));

        //As user management (login and registration) might be from another service, we'll insert all the uuids that do not exist.
        insertNewUsers(userUuidMap, adminDto, inviteesDto);

        return userUuidMap;
    }

    //If userUuid does not exist in DB, it will be created as new record.
    private void insertNewUsers (Map<UUID, User> userUuidMap, UserDto adminDto, List<UserDto> inviteesDto) {
        var newUsersDto = new ArrayList<UserDto>();
        if(!userUuidMap.containsKey(adminDto.userUuid())) {
            newUsersDto.add(adminDto);
        }
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
        }
    }
}
