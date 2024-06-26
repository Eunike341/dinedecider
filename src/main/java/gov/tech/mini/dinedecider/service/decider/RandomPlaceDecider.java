package gov.tech.mini.dinedecider.service.decider;

import gov.tech.mini.dinedecider.repo.Submission;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RandomPlaceDecider implements PlaceDecider {
    @Override
    public Submission select(List<Submission> submissions) {
        if (submissions == null || submissions.isEmpty()) {
            return null;
        }
        Submission submission = submissions.stream()
                .skip(ThreadLocalRandom.current().nextInt(submissions.size()))
                .findFirst().get();
        return submission;
    }
}
