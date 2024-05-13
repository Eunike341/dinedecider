package gov.tech.mini.dinedecider.service;

import gov.tech.mini.dinedecider.domain.SubmissionDto;
import gov.tech.mini.dinedecider.domain.exception.ApiException;
import gov.tech.mini.dinedecider.domain.exception.ErrorCode;
import gov.tech.mini.dinedecider.repo.SessionUserRepository;
import gov.tech.mini.dinedecider.repo.Submission;
import gov.tech.mini.dinedecider.repo.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubmissionService {
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
        var sessionUser = sessionUserRepository.findByAttendee_UuidAndSession_Uuid(submissionDto.submittedBy().userUuid(), sessionUuid)
                .orElseThrow(() -> new ApiException("User not found on session", ErrorCode.USER_NOT_FOUND));
        var submission = new Submission();
        submission.setPlaceName(submissionDto.placeName());
        submission.setSessionUser(sessionUser);
        submissionRepository.save(submission);
    }
}
