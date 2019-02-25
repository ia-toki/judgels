package judgels.sandalphon.submission.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.MultipleChoiceItemConfig;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.Verdict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipleChoiceItemSubmissionGrader implements ItemSubmissionGrader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleChoiceItemSubmissionGrader.class);

    private final ObjectMapper objectMapper;

    public MultipleChoiceItemSubmissionGrader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Grading grade(Item item, String answer) {
        MultipleChoiceItemConfig config;
        try {
            config = objectMapper.readValue(item.getConfig(), MultipleChoiceItemConfig.class);
        } catch (IOException e) {
            LOGGER.error("Internal error grading item submission", e);
            return new Grading.Builder()
                    .verdict(Verdict.INTERNAL_ERROR)
                    .score(Optional.empty())
                    .build();
        }

        Optional<MultipleChoiceItemConfig.Choice> matchingChoice = config.getChoices().stream()
                .filter(c -> c.getAlias().equals(answer))
                .findAny();

        if (matchingChoice.isPresent() && matchingChoice.get().getIsCorrect()) {
            return new Grading.Builder()
                    .verdict(Verdict.ACCEPTED)
                    .score(Optional.of(config.getScore()))
                    .build();
        } else {
            return new Grading.Builder()
                    .verdict(Verdict.WRONG_ANSWER)
                    .score(Optional.of(-config.getPenalty()))
                    .build();
        }
    }
}
