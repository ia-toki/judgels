package judgels.sandalphon.submission.programming;

import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import judgels.sandalphon.api.submission.programming.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmissionRegrader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionRegrader.class);

    private final SubmissionStore submissionStore;
    private final ExecutorService executorService;
    private final SubmissionRegradeProcessor processor;

    public SubmissionRegrader(
            SubmissionStore submissionStore,
            ExecutorService executorService,
            SubmissionRegradeProcessor processor) {

        this.submissionStore = submissionStore;
        this.executorService = executorService;
        this.processor = processor;
    }

    public void regradeSubmission(Submission submission) {
        String gradingJid = submissionStore.createGrading(submission);
        processor.process(ImmutableMap.of(gradingJid, submission));
    }

    public void regradeSubmissions(List<Submission> submissions) {
        Map<String, Submission> submissionsMap = new LinkedHashMap<>();
        for (Submission submission : submissions) {
            String gradingJid = submissionStore.createGrading(submission);
            submissionsMap.put(gradingJid, submission);
        }

        CompletableFuture.runAsync(() -> processor.process(submissionsMap), executorService)
                .exceptionally(e -> {
                    LOGGER.error("Failed to regrade submissions", e);
                    return null;
                });
    }
}
