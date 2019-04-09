package judgels.sandalphon.submission.bundle;

import java.util.Optional;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ShortAnswerItemConfig;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.Verdict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShortAnswerItemSubmissionGrader implements ItemSubmissionGrader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShortAnswerItemSubmissionGrader.class);

    @Override
    public Grading grade(Item item, String answer) {
        ShortAnswerItemConfig config;
        try {
            config = (ShortAnswerItemConfig) item.getConfig();
            String gradingRegex = config.getGradingRegex().orElse("");

            if (gradingRegex.isEmpty()) {
                return new Grading.Builder()
                        .verdict(Verdict.PENDING_MANUAL_GRADING)
                        .score(Optional.empty())
                        .build();
            }

            if (answer.matches(gradingRegex)) {
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
        } catch (RuntimeException e) {
            LOGGER.error("Internal error grading item submission", e);
            return new Grading.Builder()
                    .verdict(Verdict.INTERNAL_ERROR)
                    .score(Optional.empty())
                    .build();
        }
    }
}
