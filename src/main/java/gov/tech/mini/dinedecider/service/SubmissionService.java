package gov.tech.mini.dinedecider.service;

import gov.tech.mini.dinedecider.domain.SubmissionDto;
import gov.tech.mini.dinedecider.domain.exception.ApiException;
import gov.tech.mini.dinedecider.domain.exception.ErrorCode;
import gov.tech.mini.dinedecider.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubmissionService {
    private static final Logger LOG = LoggerFactory.getLogger(SubmissionService.class);
    private final SubmissionRepository submissionRepository;
    private final SessionUserRepository sessionUserRepository;

    public SubmissionService(SubmissionRepository submissionRepository, SessionUserRepository sessionUserRepository) {
        this.submissionRepository = submissionRepository;
        this.sessionUserRepository = sessionUserRepository;
    }

    public List<SubmissionDto> getSubmittedPlaces (UUID sessionUuid) {
        var submissions = submissionRepository.findBySessionUser_Session_Uuid(sessionUuid);
        return submissions
                .orElseGet(() -> List.of())
                .stream()
                .map(submission -> new SubmissionDto(submission))
                .collect(Collectors.toList());
    }

    public SubmissionDto getSelectedPlace (UUID sessionUuid) {
        var submission = submissionRepository.findBySelectedAndSessionUser_Session_Uuid(true, sessionUuid);
        return submission.map(SubmissionDto::new).orElse(null);
    }

    public void submitPlace (UUID sessionUuid, SubmissionDto submissionDto) {
        LOG.debug("Submitting a new place for:", sessionUuid, submissionDto);
        var sessionUser = sessionUserRepository.findByStatusAndAttendee_UuidAndSession_UuidAndSession_Status(
                MemberStatus.JOINED,
                submissionDto.submittedBy().userUuid(), sessionUuid, SessionStatus.ACTIVE)
                .orElseThrow(() -> new ApiException("User not found on session", ErrorCode.INVALID_SUBMISSION));
        var submission = new Submission(sessionUser, submissionDto.placeName(), LocalDateTime.now());
        submissionRepository.save(submission);
        LOG.debug("New place has been submitted for session:", submission);
    }
}
