package judgels.sandalphon.submission.bundle;

import java.util.Optional;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.MultipleChoiceItemConfig;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.Verdict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipleChoiceItemSubmissionGrader implements ItemSubmissionGrader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleChoiceItemSubmissionGrader.class);

    @Override
    public Grading grade(Item item, String answer) {
        MultipleChoiceItemConfig config;
        try {
            config = (MultipleChoiceItemConfig) item.getConfig();
        } catch (RuntimeException e) {
            LOGGER.error("Internal error grading item submission", e);
            return new Grading.Builder()
                    .verdict(Verdict.INTERNAL_ERROR)
                    .score(Optional.empty())
                    .build();
        }

        boolean noCorrectChoice = config.getChoices().stream().noneMatch(c -> c.getIsCorrect().orElse(false));
        if (noCorrectChoice) {
            return new Grading.Builder()
                    .verdict(Verdict.PENDING_MANUAL_GRADING)
                    .score(Optional.empty())
                    .build();
        }

        Optional<MultipleChoiceItemConfig.Choice> matchingChoice = config.getChoices().stream()
                .filter(c -> c.getAlias().equals(answer))
                .findAny();

        if (matchingChoice.isPresent() && matchingChoice.get().getIsCorrect().orElse(false)) {
            return new Grading.Builder()
                    .verdict(Verdict.ACCEPTED)
                    .score(Optional.of(config.getScore()))
                    .build();
        } else {
            return new Grading.Builder()
                    .verdict(Verdict.WRONG_ANSWER)
                    .score(Optional.of(config.getPenalty()))
                    .build();
        }
    }
}
