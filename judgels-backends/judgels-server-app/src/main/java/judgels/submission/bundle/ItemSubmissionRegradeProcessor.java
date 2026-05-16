package judgels.submission.bundle;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import judgels.api.problem.bundle.Item;
import judgels.api.submission.bundle.Grading;
import judgels.api.submission.bundle.ItemSubmission;
import judgels.problem.ProblemService;

public class ItemSubmissionRegradeProcessor {
    private final ItemSubmissionGraderRegistry itemSubmissionGraderRegistry;
    private final ItemSubmissionStore itemSubmissionStore;
    private final ProblemService problemService;

    @Inject
    public ItemSubmissionRegradeProcessor(
            ItemSubmissionGraderRegistry itemSubmissionGraderRegistry,
            ItemSubmissionStore itemSubmissionStore,
            ProblemService problemService) {

        this.itemSubmissionGraderRegistry = itemSubmissionGraderRegistry;
        this.itemSubmissionStore = itemSubmissionStore;
        this.problemService = problemService;
    }

    @UnitOfWork
    public void process(List<ItemSubmission> submissions) {
        for (ItemSubmission submission : submissions) {
            Optional<Item> item = problemService.getItem(submission.getProblemJid(), submission.getItemJid());
            if (item.isPresent()) {
                Grading grading = itemSubmissionGraderRegistry
                        .get(item.get().getType())
                        .grade(item.get(), submission.getAnswer());

                itemSubmissionStore.updateGrading(submission.getJid(), grading);
            }
        }
    }
}
