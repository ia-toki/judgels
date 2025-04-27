package judgels.sandalphon.submission.bundle;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import judgels.sandalphon.SandalphonClient;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemSubmissionRegradeProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemSubmissionRegradeProcessor.class);

    private final ItemSubmissionGraderRegistry itemSubmissionGraderRegistry;
    private final ItemSubmissionStore itemSubmissionStore;
    private final SandalphonClient sandalphonClient;

    @Inject
    public ItemSubmissionRegradeProcessor(
            ItemSubmissionGraderRegistry itemSubmissionGraderRegistry,
            ItemSubmissionStore itemSubmissionStore,
            SandalphonClient sandalphonClient) {

        this.itemSubmissionGraderRegistry = itemSubmissionGraderRegistry;
        this.itemSubmissionStore = itemSubmissionStore;
        this.sandalphonClient = sandalphonClient;
    }

    @UnitOfWork
    public void process(List<ItemSubmission> submissions) {
        for (ItemSubmission submission : submissions) {
            Optional<Item> item = sandalphonClient.getItem(submission.getProblemJid(), submission.getItemJid());
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
