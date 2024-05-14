package gov.tech.mini.dinedecider;

import gov.tech.mini.dinedecider.controller.SessionController;
import gov.tech.mini.dinedecider.controller.SubmissionController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DinedeciderApplicationTests {
	@Autowired
	private SessionController sessionController;

	@Autowired
	private SubmissionController submissionController;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(sessionController);
		Assertions.assertNotNull(submissionController);
	}

}
