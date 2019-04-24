package judgels.sandalphon.submission.bundle;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.problem.ProblemClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemSubmissionRegradeProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemSubmissionRegradeProcessor.class);

    private final ItemSubmissionGraderRegistry itemSubmissionGraderRegistry;
    private final ItemSubmissionStore itemSubmissionStore;
    private final ProblemClient problemClient;

    @Inject
    public ItemSubmissionRegradeProcessor(
            ItemSubmissionGraderRegistry itemSubmissionGraderRegistry,
            ItemSubmissionStore itemSubmissionStore,
            ProblemClient problemClient) {

        this.itemSubmissionGraderRegistry = itemSubmissionGraderRegistry;
        this.itemSubmissionStore = itemSubmissionStore;
        this.problemClient = problemClient;
    }

    @UnitOfWork
    public void process(List<ItemSubmission> submissions) {
        for (ItemSubmission submission : submissions) {
            Optional<Item> item = problemClient.getItem(submission.getProblemJid(), submission.getItemJid());
            if (item.isPresent()) {
                Grading grading = itemSubmissionGraderRegistry
                        .get(item.get().getType())
                        .grade(item.get(), submission.getAnswer());

                itemSubmissionStore.updateGrading(submission.getJid(), grading);

                Grading previousGrading = submission.getGrading().get();
                LOGGER.info(
                        "Regraded submission {}: verdict {}, score {} (previous verdict {}, previous score {})",
                        submission.getJid(), grading.getVerdict(), grading.getScore(),
                        previousGrading.getVerdict(), previousGrading.getScore()
                );
            } else {
                LOGGER.error("Missing problem item info for submission {}, regrade skipped", submission.getItemJid());
            }
        }
    }
}
