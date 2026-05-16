package judgels.submission.bundle;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import judgels.api.problem.bundle.Item;
import judgels.api.submission.bundle.Grading;
import judgels.api.submission.bundle.ItemSubmission;
import judgels.sandalphon.SandalphonClient;

public class ItemSubmissionRegradeProcessor {
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
            }
        }
    }
}
