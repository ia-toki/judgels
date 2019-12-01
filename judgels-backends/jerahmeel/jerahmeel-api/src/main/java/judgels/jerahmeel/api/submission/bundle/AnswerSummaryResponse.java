package judgels.jerahmeel.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.jerahmeel.api.submission.SubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAnswerSummaryResponse.class)
public interface AnswerSummaryResponse extends judgels.sandalphon.api.submission.bundle.AnswerSummaryResponse {
    SubmissionConfig getConfig();

    class Builder extends ImmutableAnswerSummaryResponse.Builder {}
}
