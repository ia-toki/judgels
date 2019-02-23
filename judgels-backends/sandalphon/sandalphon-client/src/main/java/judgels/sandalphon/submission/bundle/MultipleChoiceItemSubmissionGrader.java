package judgels.sandalphon.submission.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.MultipleChoiceItemConfig;
import judgels.sandalphon.api.submission.bundle.ItemSubmission.Grading;
import judgels.sandalphon.api.submission.bundle.Verdicts;

public class MultipleChoiceItemSubmissionGrader implements ItemSubmissionGrader {
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
            return new Grading.Builder()
                    .verdict(Verdicts.INTERNAL_ERROR)
                    .score(Optional.empty())
                    .build();
        }

        Optional<MultipleChoiceItemConfig.Choice> matchingChoice = config.getChoices().stream()
                .filter(c -> c.getAlias().equals(answer))
                .findAny();

        if (matchingChoice.isPresent() && matchingChoice.get().getIsCorrect()) {
            return new Grading.Builder()
                    .verdict(Verdicts.ACCEPTED)
                    .score(Optional.of(config.getScore()))
                    .build();
        } else {
            return new Grading.Builder()
                    .verdict(Verdicts.WRONG_ANSWER)
                    .score(Optional.of(-config.getPenalty()))
                    .build();
        }
    }
}
