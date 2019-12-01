package judgels.uriel.api.contest.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.sandalphon.api.submission.bundle.AnswerSummaryResponse;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestantAnswerSummaryResponse.class)
public interface ContestantAnswerSummaryResponse extends AnswerSummaryResponse {
    ContestSubmissionConfig getConfig();

    class Builder extends ImmutableContestantAnswerSummaryResponse.Builder {}
}
