package judgels.uriel.api.contest.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestantAnswersResponse.class)
public interface ContestantAnswersResponse {
    Map<String, List<ItemSubmission>> getAnswers();
    ContestSubmissionConfig getConfig();
    Map<String, String> getProblemAliasesMap();

    class Builder extends ImmutableContestantAnswersResponse.Builder {}
}
