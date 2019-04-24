package judgels.sandalphon.submission.bundle;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemSubmissionRegrader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemSubmissionRegrader.class);
    private static final int BATCH_SIZE = 50;

    private final ItemSubmissionStore itemSubmissionStore;
    private final ExecutorService executorService;
    private final ItemSubmissionRegradeProcessor processor;

    public ItemSubmissionRegrader(
            ItemSubmissionStore itemSubmissionStore,
            ExecutorService executorService,
            ItemSubmissionRegradeProcessor processor) {

        this.itemSubmissionStore = itemSubmissionStore;
        this.executorService = executorService;
        this.processor = processor;
    }

    public void regradeSubmission(ItemSubmission itemSubmission) {
        processor.process(ImmutableList.of(itemSubmission));
    }

    public void regradeSubmissions(
            Optional<String> containerJid,
            Optional<String> userJid,
            Optional<String> problemJid) {

        List<ItemSubmission> itemSubmissions = itemSubmissionStore.markSubmissionsForRegrade(
                containerJid, userJid, problemJid, Optional.of(BATCH_SIZE));

        for (int i = 0; i < itemSubmissions.size(); i += BATCH_SIZE) {
            int batchEndIndex = Math.min(i + BATCH_SIZE, itemSubmissions.size());
            List<ItemSubmission> itemSubmissionsBatch = itemSubmissions.subList(i, batchEndIndex);
            CompletableFuture.runAsync(() -> processor.process(itemSubmissionsBatch), executorService)
                    .exceptionally(e -> {
                        LOGGER.error("Failed to regrade submissions", e);
                        return null;
                    });
        }
    }
}
