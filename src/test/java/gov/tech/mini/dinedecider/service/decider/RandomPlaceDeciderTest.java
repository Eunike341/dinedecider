package gov.tech.mini.dinedecider.service.decider;

import gov.tech.mini.dinedecider.repo.Submission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class RandomPlaceDeciderTest {
    private RandomPlaceDecider placeDecider;

    @BeforeEach
    public void setUp() {
        placeDecider = new RandomPlaceDecider();
    }

    @Test
    public void testSelectWithEmptyList() {
        var result = placeDecider.select(List.of());
        assertNull(result, "Expected null when the list is empty");
    }

    @Test
    public void testSelectWithOneElement() {
        var expected = new Submission("Place 1");
        Submission result = placeDecider.select(List.of(expected));
        assertSame(expected, result, "Expected the single element to be selected");
    }

    @Test
    public void testSelectWithMultipleElements() {
        Submission submission1 = new Submission("Place 1");
        Submission submission2 = new Submission("Place 2");
        Submission submission3 = new Submission("Place 3");
        List<Submission> submissions = List.of(submission1, submission2, submission3);

        // Map to track selection frequency of each submission
        Map<Submission, Integer> selectionFrequency = submissions.stream()
                .collect(Collectors.toMap(submission -> submission, submission -> 0));

        int iterations = 1000;

        IntStream.range(0, iterations).forEach(i -> {
            Submission selected = placeDecider.select(submissions);
            selectionFrequency.put(selected, selectionFrequency.get(selected) + 1);
        });

        // Assert that each submission has been selected at least once
        selectionFrequency.forEach((submission, count) -> {
            assertTrue(count > 0, "Each submission should be selected at least once, but " + submission.getPlaceName() + " was not.");
        });
    }

}
