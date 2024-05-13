package gov.tech.mini.dinedecider.service.decider;

import gov.tech.mini.dinedecider.repo.Submission;

import java.util.List;

public interface PlaceDecider {
    Submission select(List<Submission> submissions);
}
