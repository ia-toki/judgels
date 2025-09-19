package judgels.sandalphon.submission.bundle;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;

public class ItemSubmissionRegrader {
    private final ItemSubmissionStore itemSubmissionStore;
    private final ItemSubmissionRegradeProcessor processor;

    @Inject
    public ItemSubmissionRegrader(
            ItemSubmissionStore itemSubmissionStore,
            ItemSubmissionRegradeProcessor processor) {

        this.itemSubmissionStore = itemSubmissionStore;
        this.processor = processor;
    }

    public void regradeSubmission(ItemSubmission itemSubmission) {
        processor.process(List.of(itemSubmission));
    }

    public void regradeSubmissions(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid) {

        List<ItemSubmission> itemSubmissions = itemSubmissionStore.getSubmissionsForRegrade(
                containerJid, userJid, problemJid);
        processor.process(itemSubmissions);
    }
}
